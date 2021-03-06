package com.sv.mc.controller;

import com.sv.mc.pojo.BranchEntity;
import com.sv.mc.pojo.HeadQuartersEntity;
import com.sv.mc.pojo.PlaceEntity;
import com.sv.mc.service.HeadQuartersService;
import com.sv.mc.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.util.List;

/**
 * 总公司控制层
 */
@RestController
public class HeadQuartersController {
    //注入
    @Autowired
    private HeadQuartersService headQuartersService;
    @Autowired
    private PlaceService placeService;

    /**
     * 全部查询
     * @return 返回所有总公司内容
     */
    @GetMapping(value = "/headMgr/allHeadQuarters")
    public @ResponseBody
    List<HeadQuartersEntity> getAllHeadQuarters() {
        return this.headQuartersService.findAllEntities();
    }

    /**
     * 更改总公司id更改数据
     * @param id 总公司Id
     * @param headQuarters 总公司数据
     * @return 修改总公司的数据
     */
    @RequestMapping(value = "/headMgr/update/headQuarters",method = RequestMethod.POST)
    public @ResponseBody
    HeadQuartersEntity updateBranchDataById(@PathParam("id") int id,@RequestBody HeadQuartersEntity headQuarters){
        return headQuartersService.save(headQuarters);
    }

    /**
     * 跳转到总公司管理页面
     * @return 总公司管理页面
     * @auther liaozhiqiang
     * @date 2018/7/11
     */
    @GetMapping(value="/headMgr/turnToHeadMgr")
    public ModelAndView turnToHeadMgr(){
        return new ModelAndView("./baseInfo/headQuartersMgr");
    }

    /**
     * 全部查询
     * @return 返回所有总公司内容分页
     * @param page 起始个数
     * @param pageSize  截至个数
     * @param session  用户信息
     */
    @GetMapping(value = "/headMgr/allHeadByPage")
    public @ResponseBody
    String getAllHeadByPage(@Param("page") String page, @Param("pageSize") String pageSize, HttpSession session) {
        return this.headQuartersService.findAllHeadByPage(Integer.parseInt(page),Integer.parseInt(pageSize),session);
    }

    /**
     * 全部查询
     * @return 返回所有总公司内容不分页
     */
    @GetMapping(value = "/headMgr/allHead")
    public @ResponseBody
    List<HeadQuartersEntity> getAllHead() {
        return this.headQuartersService.findAllHead();
    }

    /**
     * 插入一条总公司数据
     * @param headQuartersEntity 总公司数据
     * @return 插入总公司数据
     */
    @RequestMapping(value = "/headMgr/insertHead",method = RequestMethod.POST)
    public @ResponseBody
    HeadQuartersEntity insertHead(HeadQuartersEntity headQuartersEntity){
        return  this.headQuartersService.insertHead(headQuartersEntity);
    }

    /**
     * 更改总公司数据
     * @param headQuartersEntity 修改的总公司数据
     * @return 总公司数据
     */
    @RequestMapping(value = "/headMgr/updateHead",method = RequestMethod.POST)
    public @ResponseBody
    HeadQuartersEntity updateHead(HeadQuartersEntity headQuartersEntity){
        return this.headQuartersService.updateHeadDataById(headQuartersEntity);

    }

    /**
     * 逻辑删除总公司数据
     * @param id 总公司Id
     */
    @RequestMapping(value = "/headMgr/deleteHead",method = RequestMethod.POST)
    public @ResponseBody
    void deleteHead(int id){
        this.headQuartersService.deleteHead(id);
    }


    /**
     * 根据分公司id查询总公司名称
     * @return 查询的条数
     * @param branchId 分公司ID
     */
    @PostMapping(value = "/headMgr/findHeadInfo")
    public @ResponseBody
    int findHeadInfo(int branchId) {
        return this.headQuartersService.findHeadByBranchId(branchId).getId();
    }

    /**
     * 根据总公司id查询总公司名称
     * @param headId 总公司Id
     * @return 总公司名称
     */
    @PostMapping(value = "/headMgr/findHeadName")
    public @ResponseBody
    String findHeadName(int headId) {
        return this.headQuartersService.findHeadQuartersById(headId).getName();
    }


    /**
     * 根据总公司id查询下面的场地
     * @param headId 总公司Id
     * @return 场地信息
     */
    @GetMapping(value = "/headMgr/findAllPlaceByHeadId")
    public @ResponseBody
    List<PlaceEntity> findAllPlaceByHeadId(@Param("headId")int headId) {
        return this.headQuartersService.findAllPlaceByHeadId(headId);
    }

    /**
     * 查询所有未绑定的场地
     * @return 查询到的未绑定场地
     */
    @GetMapping(value = "/headMgr/findAllUnboundPlace")
    public @ResponseBody
    List<PlaceEntity> findAllUnboundPlace() {
        return this.headQuartersService.findAllUnboundPlace();
    }



    /**
     * 根据总公司id查询下面的合同
     * @param headId 总公司Id
     * @return 合同信息
     */
    @GetMapping(value = "/headMgr/findContractByHeadId")
    public @ResponseBody
    String findContractByHeadId(@Param("headId")int headId) {
        return this.headQuartersService.findContractByHeadId(headId);
    }

    /**
     * 根据总公司id查询历史合同
     * @param headId 总公司id
     * @return 历史合同
     */
    @GetMapping(value = "/headMgr/findHistoryContractByHeadId")
    public @ResponseBody
    String findHistoryContractByHeadId(@Param("headId")int headId) {
        return this.headQuartersService.findHistoryContractByHeadId(headId);
    }


    /**
     * 总公司绑定场地
     * @param headId  总公司Id
     * @param placeId  场地id
     */
    @PostMapping(value="/headMgr/headBoundPlace")
    public @ResponseBody
    void headBoundPlace(@Param("headId")int headId,@Param("placeId")int placeId){
//        String result = "绑定成功！";
//        String agreement = this.placeService.findPlaceById(placeId).getFile();
//        if(agreement==null){
//            result="请上传协议后再进行绑定操作！";
//            return result;
//        }
        this.headQuartersService.headBoundPlace(headId,placeId);
//        return result;
    }


    /**
     * 解绑场地
     * @param placeId 场地Id
     * @param supId 上级id
     * @param flagId 等级Id
     */
    @PostMapping(value="/headMgr/unboundPlace")
    public @ResponseBody
    void unboundPlace(@Param("placeId")int placeId,@Param("supId")int supId,@Param("flagId")int flagId){
//        String result = "解绑成功！";
        this.headQuartersService.unboundPlace(placeId,supId,flagId);
//        return result;
    }
































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
