package com.sv.mc.controller;

import com.sv.mc.pojo.DeviceEntity;
import com.sv.mc.pojo.PlaceEntity;
import com.sv.mc.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;

@RestController
public class PlaceController {
    //注入
    @Autowired
    private PlaceService placeService;

    /**
     * 全部查询
     * @return 返回所有场地内容
     */
    @GetMapping(value = "/placeMgr/allPlace")
    public @ResponseBody
    List<PlaceEntity> getAll() {
        return this.placeService.findAllEntities();
    }
    /**
     * 根据场地id查询单个场地内容
     * @param id
     * @return 单个分公司内容
     */
    @RequestMapping(value = "/placeMgr/place",method=RequestMethod.GET)
    public @ResponseBody
    PlaceEntity getPlaceById(@PathParam("id") int id ) {
            return this.placeService.findPlaceById(id);
    }

//    /**
//     * 插入一条场地数据
//     * @param place
//     * @return
//     */
//    @RequestMapping(value = "/place/insert",method = RequestMethod.POST)
//    public @ResponseBody
//    PlaceEntity insertPlace(@RequestBody PlaceEntity place){
//        return  placeService.insertPlace(place);
//    }
    /**
     * 更改分场地id更改数据
     * @param id
     * @param place
     * @return
     */
    @RequestMapping(value = "/placeMgr/place/update",method = RequestMethod.POST)
    public @ResponseBody
    PlaceEntity updatePlace(@PathParam("id") int id,@RequestBody PlaceEntity place){
        return placeService.updatePlaceById(id,place);

    }

    /**
     * 跳转到场地管理页面
     * @return
     * @auther liaozhiqiang
     * @date 2018/7/11
     */
    @GetMapping(value="/turnToPlaceMgr")
    public ModelAndView turnToPlaceMgr(){
        return new ModelAndView("./baseInfo/placeMgr");
    }


//    /**
//     * 全部查询
//     * @return 返回所有场地内容
//     */
//    @GetMapping(value = "/getAllPlace")
//    public @ResponseBody
//    String getAllPlace(String page, String pageSize) {
//        return this.placeService.findAllPlaceByPage(Integer.parseInt(page),Integer.parseInt(pageSize));
//    }

    /**
     * 全部查询
     * @return 返回所有场地内容
     */
    @GetMapping(value = "/placeMgr/getAllPlace")
    public @ResponseBody
    String getAllPlace() {
        return this.placeService.findAllPlace();
    }

    /**
     * 插入一条场地数据
     * @param placeEntity
     * @return
     */
    @RequestMapping(value = "/placeMgr/insertPlace",method = RequestMethod.POST)
    public @ResponseBody
    PlaceEntity insertPlace(@RequestBody PlaceEntity placeEntity){
        return  this.placeService.insertPlace(placeEntity);
    }

    /**
     * 更改场地数据
     * @param placeEntity
     * @return
     */
    @RequestMapping(value = "/placeMgr/updatePlace",method = RequestMethod.POST)
    public @ResponseBody
    PlaceEntity updatePlace(@RequestBody PlaceEntity placeEntity){
        return this.placeService.updatePlace(placeEntity);
    }

    /**
     * 插入一条场地数据
     * @param placeEntity
     * @return
     */
    @RequestMapping(value = "/placeMgr/insertPlaceChild",method = RequestMethod.POST)
    public @ResponseBody
    PlaceEntity insertPlaceChild(@RequestBody PlaceEntity placeEntity){
        return  this.placeService.insertPlaceChild(placeEntity);
    }

    /**
     * 更改场地数据
     * @param placeEntity
     * @return
     */
    @RequestMapping(value = "/placeMgr/updatePlaceChild",method = RequestMethod.POST)
    public @ResponseBody
    PlaceEntity updatePlaceChild(@RequestBody PlaceEntity placeEntity){
        return this.placeService.updatePlaceChild(placeEntity);
    }

    /**
     * 逻辑删除场地数据
     */
    @RequestMapping(value = "/placeMgr/deletePlace",method = RequestMethod.POST)
    public @ResponseBody
    void deletePlace(@RequestBody Map<String,Object> map){
        this.placeService.deletePlace(Integer.parseInt(map.get("placeId").toString()));
    }


    /**
     * 根据场地id查询他的字节点
     */
    @GetMapping(value = "/placeMgr/findPlaceByParentId")
    public @ResponseBody
    String findPlaceByParentId(@RequestParam(name = "placeId")int placeId){
        return this.placeService.findPlaceByParentId(placeId);
    }


    @GetMapping("/place/findDeviceByPlace")
    public List<DeviceEntity> findDeviceByPlace1(@RequestParam("placeId") int placeId){
        return this.placeService.findDeviceByPlaceId(placeId);
    }


    @GetMapping("/place/findDeviceByPlaceId")
    public List<DeviceEntity> findDeviceByPlace(@RequestParam("placeId") int placeId){
        return this.placeService.findDeviceByPlace(placeId);
    }
//    /**
//     * 根据场地id查询所有设备
//     */
//    @RequestMapping(value = "/placeMgr/findAllDeviceByPlaceId",method = RequestMethod.GET)
//    public @ResponseBody
//    List findAllDeviceByPlaceId(@RequestParam(name = "placeId")  int placeId){
//        return this.placeService.findDeviceByPlace(placeId);
//    }




//    /**
//     * 插入一条场地数据
//     * @param placeEntity
//     * @return
//     */
//    @RequestMapping(value = "/insertPlace",method = RequestMethod.POST)
//    public @ResponseBody
//    PlaceEntity insertPlace(PlaceEntity placeEntity,String startDateTime,String endDateTime,BindingResult bindingResult){
//        return  this.placeService.insertPlace(placeEntity,startDateTime,endDateTime);
//    }
//
//    /**
//     * 更改场地数据
//     * @param placeEntity
//     * @return
//     */
//    @RequestMapping(value = "/updatePlace",method = RequestMethod.POST)
//    public @ResponseBody
//    PlaceEntity updatePlace(PlaceEntity placeEntity,String startDateTime,String endDateTime,BindingResult bindingResult){
//        return this.placeService.updatePlace(placeEntity,startDateTime,endDateTime);
//
//    }
//
//    /**
//     * 逻辑删除场地数据
//     */
//    @RequestMapping(value = "/deletePlace",method = RequestMethod.POST)
//    public @ResponseBody
//    void deletePlace(PlaceEntity placeEntity,BindingResult bindingResult){
//        this.placeService.deletePlace(placeEntity.getId());
//    }




























    /**                                                                         失效代码
     *
     * 更改分公司名称
     * @param id 分公司id  , newName 新名字
     * @return BranchEntity
    @RequestMapping(value = "/branch/updateBranchName",method=RequestMethod.POST)
    public @ResponseBody BranchEntity updateBranchNameById(@PathParam("id")int id ,@RequestBody String newName) {
        BranchEntity result = new BranchEntity();
            result = branchService.updateBranchNameById(id,newName);
        return result;
    }
    /**
     * 更改分公司地址
     * @param id 分公司id , newAddress 新办公地址
     * @return BranchEntity
    @RequestMapping(value = "/branch/updateBranchAddress",method=RequestMethod.POST)
    public @ResponseBody
    BranchEntity updateBranchAddressById(@PathParam("id")int id ,@RequestBody String newAddress) {
        BranchEntity result = new BranchEntity();
        result = branchService.updateBranchAddressById(id,newAddress);
        return result;
    }
    /**
     * 更改分公司负责人
     * @param id 分公司id , newPrincipal 新分公司负责人
     * @return BranchEntity
    @RequestMapping(value = "/branch/updateBranchPrincipal",method=RequestMethod.POST)
    public @ResponseBody
    BranchEntity updateBranchPrincipalById(@PathParam("id")int id ,@RequestBody String newPrincipal) {
        BranchEntity result = new BranchEntity();
        result = branchService.updateBranchPrincipalById(id,newPrincipal);
        return result;
    }
    /**
     * 更改分公司联系电话
     * @param id 分公司id , newTelephone新分公司联系电话
     * @return BranchEntity
    @RequestMapping(value = "/branch/updateBranchTelephone",method=RequestMethod.POST)
    public @ResponseBody
    BranchEntity updateBranchTelephoneById(@PathParam("id")int id ,@RequestBody String newTelephone) {
        BranchEntity result = new BranchEntity();
        result = branchService.updateBranchTelephoneById(id,newTelephone);
        return result;
    }
    /**
     * 更改分公司联系邮箱
     * @param id 分公司id , newEmail新分公司联系邮箱
     * @return BranchEntity
    @RequestMapping(value = "/branch/updateBranchEmail",method=RequestMethod.POST)
    public @ResponseBody
    BranchEntity updateBranchEmailById(@PathParam("id")int id ,@RequestBody String newEmail) {
        BranchEntity result = new BranchEntity();
        result = branchService.updateBranchEmailById(id,newEmail);
        return result;
    }
    /**
     * 更该分公司事物状态
     * @param id 分公司id
     * @param newDeleteID delete_id
     * @return RequestBody
    @RequestMapping(value = "/branch/updateBranchAffair",method =RequestMethod.POST)
    public @ResponseBody
    BranchEntity updateBranchAffairById(@PathParam("id") int id,@RequestBody int newDeleteID){
        BranchEntity result = new BranchEntity();
        result = branchService.updateBranchAffairById(id,newDeleteID);
        return result;
    }
    */

}
