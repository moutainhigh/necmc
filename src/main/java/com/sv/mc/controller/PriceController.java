package com.sv.mc.controller;

import com.sv.mc.pojo.DeviceEntity;
import com.sv.mc.pojo.PlaceEntity;
import com.sv.mc.pojo.PriceEntity;
import com.sv.mc.pojo.PriceHistoryEntity;
import com.sv.mc.service.PriceHistoryService;
import com.sv.mc.service.PriceService;
import com.sv.mc.service.impl.PriceHistoryServiceImpl;
import com.sv.mc.service.impl.PriceServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PriceController {

    @Resource
    private PriceService priceService;

    @Resource
    private PriceHistoryService priceHistoryService;

    /**
     * 分页查询价格列表
     * @param page 当前开始页码（代码中从0开始）
     * @param pageSize 页号
     * @return
     */
    @GetMapping("/price/pageall")
    public Page<PriceEntity> findAllPagePrice(@Param("page") String page, @Param("pageSize") String pageSize){

        PageRequest pageRequest =new PageRequest(Integer.parseInt(page)-1,Integer.parseInt(pageSize));
        Page<PriceEntity> priceEntityPage = this.priceService.findAllPagePrice(pageRequest);
        return priceEntityPage;
    }

    /**
     * 不分页查询价格列表,价格状态为正常的
     * @return 价格集合
     */
    @GetMapping("/price/status")
    public List<PriceEntity> findStatusPrice(){
        List<PriceEntity> priceList = priceService.findStatusPrice();
        for (PriceEntity priceEntity:priceList) {
            int useTime = priceEntity.getUseTime()/60;
            priceEntity.setUseTime(useTime);
        }
        return priceList;
    }

    /**
     * 不分页查询价格列表
     * @return 价格集合
     */
    @GetMapping("/price/all")
    public List<PriceEntity> findAllPrice(){
        List<PriceEntity> priceList = priceService.findAllPrice();
        for (PriceEntity priceEntity:priceList) {
            int useTime = priceEntity.getUseTime()/60;
            priceEntity.setUseTime(useTime);
        }
        return priceList;
    }

    /**
     * 不分页查询历史价格列表
     * @return 价格集合
     */
    @GetMapping("/price/allHistory")
    public List<PriceHistoryEntity> findAllHistoryPrice(){
        List<PriceHistoryEntity> priceList = priceHistoryService.findAllPrice();
        for (PriceHistoryEntity priceEntity:priceList) {
            int useTime = priceEntity.getUseTime()/60;
            priceEntity.setUseTime(useTime);
        }
        return priceList;
    }

    /**
     * 新建价格
     * @param priceEntity 价格对象
     */
    @PostMapping("/price/save")
    public String addPrice(@RequestBody PriceEntity priceEntity){
        this.priceService.addPrice(priceEntity);
        return "保存成功";
    }

    /**
     * 批量更新或保存价格
     * @param models 价格对象集合
     */
    @PostMapping("/price/update")
    public @ResponseBody List<PriceEntity> updatePrice(@RequestBody List<PriceEntity> models) {
        List<PriceEntity> priceEntityList = this.priceService.batchSaveOrUpdatePrice(models);
        for (PriceEntity price:priceEntityList
                ) {
            price.setUseTime(price.getUseTime()/60);
        }
        return priceEntityList;
    }

    /**
     * 批量删除价格
     * @param models 价格对象集合
     */
    @PostMapping("/price/batchDelete")
    public  List<PriceEntity> batchDeletePrice(@RequestBody List<PriceEntity> models) {
        List<PriceEntity> priceEntityList = this.priceService.batchDeletePrice(models);
        return priceEntityList;
    }

    /**
     * 删除价格
     * @param priceId 价格Id
     */
    @PostMapping("/price/delete")
    public String deletePrice(@RequestParam(name = "priceId")  int priceId){
        this.priceService.deletePrice(priceId);
        PriceEntity priceEntity = this.priceService.findPriceById(priceId);
        return "删除成功";
    }

    /**
     * 根据设备id查询价格和时长
     * @param deviceId
     * @return 当前设备的价格，时长集合
     */
    @PostMapping("/price/deviceUse")
    public List<Object[]> findPriceByDeviceId(@RequestParam int deviceId){

        return this.priceService.findPriceByDeviceId(deviceId);

    }

    /**
     * 根据机器查询当前机器已绑定价格
     * @param deviceId
     * @return
     */
    @GetMapping("/price/devicePrice")
    public List<PriceEntity> findDevicePrice(@RequestParam("deviceId") int deviceId){
        return this.priceService.findDevicePrice(deviceId);
    }

    /**
     * 查询当前设备可绑定的未绑定价格
     * @param deviceId
     * @return 价格集合
     */
    @GetMapping("/price/deviceUnPrice")
    public List<PriceEntity> findDeviceUnPrice(@RequestParam int deviceId){
        return this.priceService.findUnDevicePrice(deviceId);
    }

    /**
     * 给对应机器绑定价格
     * @param listMap 价格id集合与机器Id
     * @return 保存成功消息
     */
    @PostMapping("/price/deviceSavePrice")
    public List<PriceEntity> deviceSavePrice(@RequestBody Map<String,Object> listMap){
        return this.priceService.deviceSavePrice(listMap);
    }

    /**
     * 为机器删除其绑定的价格
     * @param listMap 价格Id与机器Id
     * @return 该机器已绑定集合
     */
    @PostMapping("/price/deviceDeletePrice")
    public List<PriceEntity> deviceDeletePrice(@RequestBody Map<String,Object> listMap){
        return  this.priceService.deviceDeletePrice(listMap);
    }

    /**
     * 为场地上某种类型的所有的机器进行价格绑定
     * @param listMap 场地id 与 价格id集合
     * @return 可绑定价格集合
     */
    @PostMapping("/price/placeAddPrice")
    public List<PriceEntity> placeAddPrice(@RequestBody Map<String,Object> listMap){
        return this.priceService.placeAddPrice(listMap);
    }


//    @GetMapping("/price/status")
//    public List<PriceEntity> priceStatus(@RequestParam int status){
//        return priceService.statusPrice(status);
//    }

    /**
     * 跳转到priceTest页面
     *
     * @return
     */
    @GetMapping(value = "/priceTest")
    public ModelAndView turnToRoleMgrTest() {
        ModelAndView mv = new ModelAndView();

        mv.setViewName("./priceDemo");
        return mv;
    }

    /**
     * 跳转到priceTest页面
     *
     * @return
     */
    @GetMapping(value = "/priceTest1")
    public ModelAndView turnTopriceMgrTest() {
        ModelAndView mv = new ModelAndView();

        mv.setViewName("./priceForplace");
        return mv;
    }



}
