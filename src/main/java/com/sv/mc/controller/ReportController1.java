package com.sv.mc.controller;

import com.sv.mc.pojo.*;
import com.sv.mc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 销售报表控制层
 */
@RestController
public class ReportController1 {
   @Autowired
    private ProvinceService ps ;

   @Autowired
  private RegionService rs;
   @Autowired
   private PlaceService pls;
   @Autowired
   private ReportViewService rvs;
   @Autowired
   private  CountService countService;
   @Autowired
   private ProvinceService provinceService;
   @Autowired
   private  CityService cityService;
   @Autowired
   private  PlaceService placeService;
    /**
     * 跳转到report页面
     *
     * @return
     */
    @GetMapping(value = "/turnToreport")
    public ModelAndView turnToBussinessMgr() {
        return new ModelAndView("./baseInfo/report");
    }


    /**
     * 查询所有省
     * @param request 用户信息
     * @return 省报表数据
     */

    @GetMapping(value = "/province")
    public List<ProvinceEntity> getProvince(HttpServletRequest request){
     List<ProvinceEntity> list= new ArrayList<>();
        UserEntity user= (UserEntity) request.getSession().getAttribute("user");
        int pId=user.getpId();
        System.out.println(pId);
        int gid=user.getGradeId();
        System.out.println(gid);
        if (user.getGradeId()==1) {
            list=ps.selectProvince();
        }else if(user.getGradeId()==2||user.getGradeId()==3){
            list=this.countService.getProvinceByP_ID(pId);
        }else{
            list=this.provinceService.getProvinceByID(pId);
        }
        return list;
    }

    /**
     * 查询所有市
     * @param request 请求信息
     * @param id 市iD
     * @return  市报表数据
     */

    @GetMapping(value = "/city1")
    public List<CityEntity> getCity2(HttpServletRequest request,@Param("id") int id){
        UserEntity user= (UserEntity) request.getSession().getAttribute("user");
       List<CityEntity>list=new ArrayList<>();
        int pId=user.getpId();
        if (user.getGradeId()==1) {
            list= rs.getRegionityCity(id);
        }else if (user.getGradeId()==2||user.getGradeId()==3){
            list=this.countService.getCityByP_ID(pId);
        }else{
            list=this.cityService.getCityByPlace_ID(pId);
        }
        return  list;
    }


    /**
     * 查询所有地区
     * @param request 用户信息
     * @param id 场地ID
     * @return  场地报表数据
     */
    @GetMapping(value = "/place")
    public List<PlaceEntity> getPlace(HttpServletRequest request,@Param("id") int id){
        UserEntity user= (UserEntity) request.getSession().getAttribute("user");

        int pId=user.getpId();
       List<PlaceEntity> list= new ArrayList<>();
        if (user.getGradeId()==1){
        list=  pls.getPlace(id);
       }else if (user.getGradeId()==2||user.getGradeId()==3){
            list=this.countService.getPlaceByP_ID(pId);
        }else {
            list=this.placeService.getPlaceBy_ID(pId);
        }
        return list;
    }



    /**
     * 根据时间省id 查询报表
     * @param s 起始时间
     * @param e 截止时间
     * @param id 省Id
     * @return 省报表数据
     */
    @GetMapping(value = "/fillday")
    public List<ReportViewEntity> getProvince1(@Param("s")Date s,@Param("e")Date e,@Param("id") int id){
      return  rvs.fillDayReport(s, e, id);
    }


    /**
     *  查询市报表
     * @param s 起始时间
     * @param e 截止时间
     * @param id 市id
     * @return 市报表数据
     */
    @GetMapping(value = "/fillcity")
    public List<ReportViewEntity> getCity1(@Param("s")Date s,@Param("e")Date e,@Param("id") int id){
        return  rvs.fillcityReport(s, e, id);


    }

    /**
     *  查询市报表
     * @param s 起始时间
     * @param e 截止时间
     * @param id 市Id
     * @return 报表数据
     */
    @GetMapping(value = "/fillplace")
    public List<ReportViewEntity> getPlace1(@Param("s")Date s,@Param("e")Date e,@Param("id") int id){
        return  rvs.fillplaceReport(s, e, id);
    }







}
