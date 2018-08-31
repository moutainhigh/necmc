package com.sv.mc.controller;

import com.sv.mc.pojo.DeviceEntity;
import com.sv.mc.pojo.GatewayEntity;
import com.sv.mc.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class GatewayController {
    @Autowired
    private GatewayService gatewayService;

    /**
     * 跳转到网关管理页面
     *
     * @return
     */
    @GetMapping(value = "/gatewayMgr/turnToGatewayMgr")
    public ModelAndView turnToGatewayMgr() {
        return new ModelAndView("./baseInfo/gatewayMgr");
    }


    /**
     * 查询所有网关
     *
     * @return 返回所有设备内容
     */
    @GetMapping(value = "/gatewayMgr/allGateway")
    public @ResponseBody
    List<GatewayEntity> getAllGateway() {
        return this.gatewayService.findAllEntities();
    }

    /**
     * 添加网关
     */
    @PostMapping(value = "/gatewayMgr/insertGateway")
    public @ResponseBody
    GatewayEntity insertGateway(GatewayEntity gatewayEntity) {
        return this.gatewayService.save(gatewayEntity);
    }

    /**
     * 修改网关信息
     */
    @PostMapping(value = "/gatewayMgr/updateGateway")
    public @ResponseBody
    GatewayEntity updateGateway(GatewayEntity gatewayEntity) {
        return this.gatewayService.updateGatewayInfo(gatewayEntity);
    }

    /**
     * 修改域名端口
     */
    @PostMapping(value = "/gatewayMgr/updateGatewayPort")
    public @ResponseBody
    void updateGatewayPort(String domainName,String port){
        try {
            this.gatewayService.updateGatewayPort(domainName,port);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 修改频道
     */
    @PostMapping(value = "/gatewayMgr/updateGatewayChannel")
    public @ResponseBody
    void updateGatewayChannel(String channel) {
        try {
            this.gatewayService.updateGatewayChannel(channel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 重启网关
     */
    @PostMapping(value = "/gatewayMgr/restartGateway")
    public @ResponseBody
    void restartGateway() {
        try {
            this.gatewayService.restartGateway();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}