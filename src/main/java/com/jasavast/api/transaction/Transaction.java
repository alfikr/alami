package com.jasavast.api.transaction;

import com.jasavast.api.utils.ParamUtils;
import com.jasavast.core.annotation.DeleteExecution;
import com.jasavast.core.annotation.PostExecution;
import com.jasavast.core.annotation.PutExecution;
import com.jasavast.core.error.*;
import com.jasavast.core.util.DBUtils;
import com.jasavast.core.util.SqlParam;
import com.jasavast.service.ApiAbstract;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component("apiTransaction")
public class Transaction extends ApiAbstract {
    @Autowired
    private ParamUtils paramUtils;
    public Transaction(DBUtils dbUtils) {
        super(dbUtils);
    }
    private final static  String sqlNasabah="select * from nasabah n where n.id =:nasabahId";
    String sqlTransaction="insert into \"transaction\" (description,nominal,tanggal,posisi,nasabah_id,ins_by,mod_by) " +
            "values (:description,:nominal, :tanggal, 'K', :nasabahId,:insBy,:modBy) " +
            "returning id,description ,nominal ,tanggal ,nasabah_id ,posisi ,ins_on ,ins_by ,mod_on ,mod_by ";
    @PostExecution
    public Mono<JSONObject> tabung(){
        if(reqData.getDouble("nominal")<=0){
            return Mono.error(new InvalidParameterException("Parameter nominal tidak boleh kurang dari sama dengan 0"));
        }
        return paramUtils.getMapParam().zipWith(dbUtils.executeQuery("select * from nasabah",new ArrayList<>()))
                .flatMap(tuple->{
                    if (tuple.getT2()==null){
                        return Mono.error(new NasabahNotFoundException());
                    }
                    tuple.getT1().add(new SqlParam("nominal", Optional.ofNullable(reqData.getDouble("nominal")).orElse(0.0)));
                    tuple.getT1().add(new SqlParam("tanggal",reqData.has("tanggalTransaksi")? reqData.getString("tanggalTransaksi").isEmpty()?LocalDateTime.now():
                            LocalDateTime.parse(reqData.getString("tanggalTransaksi")) :LocalDateTime.now()));
                    tuple.getT1().add(new SqlParam("description","Tabungan "+tuple.getT2().getString("firstName")));
                    tuple.getT1().add(new SqlParam("nasabahId",Optional.ofNullable(reqData.getString("nasabahId")).orElse("")));
                    return dbUtils.executeQuery(sqlTransaction,tuple.getT1()).zipWith(Mono.just(tuple.getT2()));
                })
                .map(tupple->{
                    JSONObject obj = new JSONObject();
                    obj.put("nasabahId",tupple.getT2().getString("id"));
                    obj.put("firstName",tupple.getT2().getString("firstName"));
                    obj.put("lastName",Optional.ofNullable(tupple.getT2().getString("lastName")).orElse(""));
                    obj.put("nominal",Optional.ofNullable(tupple.getT1().getDouble("nominal")).orElse(0.0));
                    obj.put("tanggalTransaksi",tupple.getT1().get("tanggal"));
                    return obj;
                }).flatMap(jsonObject -> {
                    //get saldo
                    String saldoSql="select n.id,n.first_name ,n.last_name,((coalesce(k.total,0))- (coalesce (d.total,0))) as saldo  from nasabah n " +
                            "left join (select sum(nominal) as total, nasabah_id from \"transaction\" t where t.posisi ='K' group by nasabah_id) k on n.id = k.nasabah_id " +
                            "left join (select sum(nominal) as total, nasabah_id from \"transaction\" t where t.posisi='D' group by nasabah_id) d on n.id=d.nasabah_id " +
                            "where n.id = :nasabahId";
                    List<SqlParam> par = new ArrayList<>();
                    par.add(new SqlParam("nasabahId",jsonObject.getString("nasabahId")));
                    return dbUtils.executeQuery(saldoSql,par).zipWith(Mono.just(jsonObject));
                }).map(tup->{
                    JSONObject obj = tup.getT2();
                    obj.put("saldo",tup.getT1().getDouble("saldo"));
                    JSONObject object=new JSONObject();
                    object.put("success",true);
                    object.put("data",obj);
                    return object;
                });
    }

    @PostExecution
    public Mono<JSONObject> pinjam(){
        //cek saldo
        if(reqData.getDouble("nominal")<=0){
            return Mono.error(new InvalidParameterException("Parameter nominal tidak boleh kurang dari sama dengan 0"));
        }
        String saldoKomunitas="select coalesce ((select sum(nominal) from \"transaction\" t where t.posisi ='K') - (select sum(nominal) from \"transaction\" t where t.posisi !='K'),0) as saldo";
        return paramUtils.getMapParam()
                .zipWith(dbUtils.executeQuery(saldoKomunitas,new ArrayList<>()))
                .flatMap(tupple->{
                    if(tupple.getT2()==null){
                        return Mono.error(new BadRequestAlertException("Saldo tidak ditemukan","saldo","400"));
                    }
                    if(tupple.getT2().getDouble("saldo")<reqData.getDouble("nominal")){
                        return Mono.error(new LoanToBigException(tupple.getT2().getDouble("saldo"),reqData.getDouble("nominal")));
                    }
                    List<SqlParam> p=new ArrayList<>();
                    p.add(new SqlParam("nasabahId",reqData.getString("nasabahId")));
                    return dbUtils.executeQuery(sqlNasabah,p).zipWith(Mono.just(tupple.getT1()));
                })
                .flatMap(tup->{
                    if (tup.getT1()==null){
                        return Mono.error(new NasabahNotFoundException());
                    }
                    tup.getT2().add(new SqlParam("description","Pinjaman"));
                    tup.getT2().add(new SqlParam("nominal",reqData.getDouble("nominal")));
                    tup.getT2().add(new SqlParam("tanggal", LocalDateTime.now()));
                    tup.getT2().add(new SqlParam("nasabahId",tup.getT1().getString("id")));
                    return dbUtils.executeQuery(sqlTransaction,tup.getT2()).zipWith(Mono.just(tup.getT1()));
                }).map(t->{
                    JSONObject data = new JSONObject();
                    data.put("firstName",t.getT2().getString("firstName"));
                    data.put("lastName",t.getT2().getString("lastName"));
                    data.put("pinjaman",t.getT1().getDouble("nominal"));
                    JSONObject obj = new JSONObject();
                    obj.put("success",true);
                    obj.put("data",data);
                    return obj;
                });
    }

    @PostExecution
    public Mono<JSONObject> pengembalian(){
        //cek pinjaman
        if(reqData.getDouble("nominal")<=0){
            return Mono.error(new InvalidParameterException("Parameter nominal tidak boleh kurang dari sama dengan 0"));
        }
        //jika tidak memiliki pinjaman throw error
        String pinjaman="select n.id,n.first_name ,n.last_name ,(coalesce (pinjam.total,0) - coalesce (kembali.total,0)) as sisa_pinjaman from nasabah n " +
                "left join (select sum(nominal) as total, nasabah_id from \"transaction\" where posisi='P' group by nasabah_id) pinjam on n.id =pinjam.nasabah_id " +
                "left join (select sum(nominal) as total, nasabah_id from \"transaction\" where posisi='C' group by nasabah_id) kembali on n.id=kembali.nasabah_id " +
                "where n.id = :nasabahId";
        List<SqlParam> nasabahParam = new ArrayList<>();
        nasabahParam.add(new SqlParam("nasabahId",reqData.getString("nasabahId")));
        return paramUtils.getMapParam().zipWith(dbUtils.executeQuery(sqlNasabah,nasabahParam))
                .flatMap(t->{
                    if(t.getT2()==null){
                        return Mono.error(new NasabahNotFoundException());
                    }
                    return dbUtils.executeQuery(pinjaman,nasabahParam)
                            .map(obj->{
                                if (obj.getDouble("sisa_pinjaman")> reqData.getDouble("nominal")){
                                    throw new SaldoNotEnoughException();
                                }
                                return obj;
                            }).zipWith(Mono.just(t));
                })
                .flatMap(t->{
                    JSONObject obj = new JSONObject();
                    obj.put("nasabahId",t.getT2().getT2().getString("id"));
                    obj.put("firstName",t.getT2().getT2().getString("firstName"));
                    obj.put("lastName",t.getT2().getT2().getString("lastName"));
                    obj.put("nominalBayar",reqData.getDouble("nominal"));
                    obj.put("utangAwal",t.getT1().getDouble("sisa_pinjaman"));
                    List<SqlParam> params=t.getT2().getT1();
                    params.add(new SqlParam("nasabahId",t.getT2().getT2().getString("id")));
                    params.add(new SqlParam("description","Pengembalian Pinjaman"));
                    params.add(new SqlParam("tanggal",LocalDateTime.now(),LocalDateTime.class));
                    params.add(new SqlParam("nominal",reqData.getString("nominal")));
                    return dbUtils.executeQuery(sqlTransaction,params).zipWith(Mono.just(obj));
                }).map(t->{
                    t.getT2().put("sisa_pinjaman",t.getT2().getDouble("utangAwal")-t.getT2().getDouble("nominalBayar"));
                    JSONObject res = new JSONObject();
                    res.put("success",true);
                    res.put("data",t.getT2());
                    return res;
                });
    }
    public Mono<JSONObject> pengambilanSaldo(){
        if(reqData.getDouble("nominal")<=0){
            return Mono.error(new InvalidParameterException("Parameter nominal tidak boleh kurang dari sama dengan 0"));
        }
        return paramUtils.getMapParam()
                .flatMap(p->{
                    List<SqlParam> params=new ArrayList<>();
                    params.add(new SqlParam("nasabahId",reqData.getString("nasabahId")));
                    return dbUtils.executeQuery(sqlNasabah,params).zipWith(Mono.just(p));
                })
                .flatMap(tup->{
                    String saldoSql="select n.id,n.first_name ,n.last_name,((coalesce(k.total,0))- (coalesce (d.total,0))) as saldo  from nasabah n " +
                            "left join (select sum(nominal) as total, nasabah_id from \"transaction\" t where t.posisi ='K' group by nasabah_id) k on n.id = k.nasabah_id " +
                            "left join (select sum(nominal) as total, nasabah_id from \"transaction\" t where t.posisi='D' group by nasabah_id) d on n.id=d.nasabah_id " +
                            "where n.id = :nasabahId";
                    List<SqlParam> params = new ArrayList<>();
                    params.add(new SqlParam("nasabahId",tup.getT1().getString("id")));
                    return dbUtils.executeQuery(saldoSql,params)
                            .map(obj->{
                                if (obj.getDouble("saldo")<= reqData.getDouble("nominal")){
                                    throw new SaldoNotEnoughException();
                                }
                                tup.getT2().add(new SqlParam("description","Ambil tabungan"));
                                tup.getT2().add(new SqlParam("nominal",reqData.getDouble("nominal"),Double.class));
                                tup.getT2().add(new SqlParam("tanggal",LocalDateTime.now(),LocalDateTime.class));
                                tup.getT2().add(new SqlParam("nasabahId",tup.getT1().getString("id")));
                                return tup;
                            });
                }).flatMap(obj->{
                    return dbUtils.executeQuery(sqlTransaction,obj.getT2()).zipWith(Mono.just(obj.getT1()));
                }).map(t->{
                    JSONObject obj=new JSONObject();
                    obj.put("nasabahId",t.getT2().getString("id"));
                    obj.put("firstName",t.getT2().getString("firstName"));
                    obj.put("lastName",t.getT2().getString("lastName"));
                    obj.put("nominal",t.getT1().getDouble("nominal"));
                    JSONObject res = new JSONObject();
                    res.put("success",true);
                    res.put("data",obj);
                    return res;
                });
    }
    @PostExecution
    public Mono<JSONObject> getTransactionByDate(){
        if(reqData.getDouble("nominal")<=0){
            return Mono.error(new InvalidParameterException("Parameter nominal tidak boleh kurang dari sama dengan 0"));
        }
        if (LocalDate.parse(req.getString("awal")).isAfter(LocalDate.now()) || LocalDate.parse(req.getString("akhir")).isAfter(LocalDate.now())){
            return Mono.error(new DateIsAfterToDayException());
        }
        List<SqlParam> params=new ArrayList<>();
        params.add(new SqlParam("tanggalAwal",LocalDate.parse(reqData.getString("awal"))));
        params.add(new SqlParam("tanggalAkhir",LocalDate.parse(reqData.getString("akhir"))));
        return dbUtils.executeFluxQuery("select t.id ,t.description ,n.first_name ,n.last_name ,t.nominal ,t.ins_on ,t.ins_by ,t.mod_by ,t.mod_on,t.posisi  " +
                        "from \"transaction\" t " +
                "left join nasabah n on t.nasabah_id =n.id " +
                "where t.tanggal between :tanggalAwal and :tanggalAkhir",params)
                .subscribeOn(Schedulers.boundedElastic())
                .map(obj->{
                    if(obj.getString("posisi").equals("D") || obj.getString("posisi").equals("P")){
                        obj.put("nominal",Math.abs(obj.getDouble("nominal")));
                    }
                    return obj;
                })
                .collectList()
                .map(l->{
                    JSONObject obj = new JSONObject();
                    obj.put("success",true);
                    obj.put("data",l);
                    return obj;
                });
    }
    @PostExecution
    public Mono<JSONObject> getTransactionByNasabah(){
        if(reqData.getDouble("nominal")<=0){
            return Mono.error(new InvalidParameterException("Parameter nominal tidak boleh kurang dari sama dengan 0"));
        }
        List<SqlParam> params=new ArrayList<>();
        params.add(new SqlParam("nasabahId",reqData.getString("nasabahId")));
        return dbUtils.executeQuery(sqlNasabah,params)
                .flatMap(n->{
                    if (n==null){
                        return Mono.error(new NasabahNotFoundException());
                    }
                    String sql="select t.id, t.description, t.nominal,t.tanggal,t.posisi  from \"transaction\" t where t.nasabah_id = :nasabahId";
                    return dbUtils.executeFluxQuery(sql,params)
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(obj->{
                                if(obj.getString("posisi").equals("D") || obj.getString("posisi").equals("P")){
                                    obj.put("nominal",Math.abs(obj.getDouble("nominal")));
                                }
                                return obj;
                            }).collectList();
                }).map(l->{
                    JSONObject obj=new JSONObject();
                    obj.put("success",true);
                    obj.put("data",l);
                    return obj;
                });
    }
}
