package com.sv.mc.controller;

import com.sv.mc.pojo.DeviceEntity;
import com.sv.mc.pojo.PlaceEntity;
import com.sv.mc.service.PlaceService;
import com.sv.mc.util.FileUtil2;
import com.sv.mc.util.SpringContextUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
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
     * 跳转到场地树状图页面
     * @return
     * @auther liaozhiqiang
     * @date 2018/7/11
     */
    @GetMapping(value="/turnToPlaceMgr")
    public ModelAndView turnToPlaceMgr(){
        return new ModelAndView("./baseInfo/placeMgr");
    }

    /**
     * 跳转到场地方管理页面
     * @return
     * @auther liaozhiqiang
     * @date 2018/7/11
     */
    @GetMapping(value="/turnToPlaceDetailMgr")
    public ModelAndView turnToPlaceDetailMgr(){
        return new ModelAndView("./baseInfo/placeDetailMgr");
    }


    /**
     * 全部查询
     * @return 返回所有场地内容
     */
    @GetMapping(value = "/placeMgr/getAllPlace")
    public @ResponseBody
    String getAllPlace(@Param("page") String page, @Param("pageSize") String pageSize) {
        return this.placeService.findAllPlaceByPage(Integer.parseInt(page),Integer.parseInt(pageSize));
    }

//    /**
//     * 全部查询
//     * @return 返回所有场地内容
//     */
//    @GetMapping(value = "/placeMgr/getAllPlace")
//    public @ResponseBody
//    String getAllPlace() {
//        return this.placeService.findAllPlace();
//    }

    /**
     * 全部查询
     * @return 返回所有场地内容
     */
    @PostMapping(value = "/placeMgr/getAllPlaceForTreelist")
    public @ResponseBody
    String getAllPlaceForTreelist(@RequestBody Map<String,Object> map) {
        return this.placeService.findAllPlace(map);
    }




    /**
     * 插入一条场地数据
     * @param
     * @return
     */
    @RequestMapping(value = "/placeMgr/insertPlace",method = RequestMethod.POST)
    public @ResponseBody
    PlaceEntity insertPlace(@RequestBody Map<String,Object> map){
        return  this.placeService.insertPlace(map);
    }

    /**
     * 更改场地数据
     * @param
     * @return
     */
    @RequestMapping(value = "/placeMgr/updatePlace",method = RequestMethod.POST)
    public @ResponseBody
    PlaceEntity updatePlace(@RequestBody Map<String,Object> map){
        return this.placeService.updatePlace(map);
    }


    /**
     * 插入一条场地数据
     * @param
     * @return
     */
    @RequestMapping(value = "/placeMgr/insertPlaceTree",method = RequestMethod.POST)
    public @ResponseBody
    PlaceEntity insertPlaceTree(@RequestBody Map<String,Object> map){
        return  this.placeService.insertPlaceTree(map);
    }

    /**
     * 更改场地数据
     * @param
     * @return
     */
    @RequestMapping(value = "/placeMgr/updatePlaceTree",method = RequestMethod.POST)
    public @ResponseBody
    PlaceEntity updatePlaceTree(@RequestBody Map<String,Object> map){
        return this.placeService.updatePlaceTree(map);
    }

    /**
     * 插入一条场地数据
     * @param
     * @return
     */
    @RequestMapping(value = "/placeMgr/insertPlaceChild",method = RequestMethod.POST)
    public @ResponseBody
    PlaceEntity insertPlaceChild(@RequestBody Map<String,Object> map){
        return  this.placeService.insertPlaceChild(map);
    }

    /**
     * 更改场地数据
     * @param
     * @return
     */
    @RequestMapping(value = "/placeMgr/updatePlaceChild",method = RequestMethod.POST)
    public @ResponseBody
    PlaceEntity updatePlaceChild(@RequestBody Map<String,Object> map){
        return this.placeService.updatePlaceChild(map);
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
    public List<DeviceEntity> findDeviceByPlace1(@RequestParam("placeId") int placeId,@RequestParam("deviceId") String deviceId){
        return this.placeService.findDeviceByPlaceId(placeId,deviceId);
    }

    /**
     * 根据场地id查询所有设备
     * @param placeId
     * @return
     */
    @GetMapping("/place/findDeviceByPlaceId")
    public String findDeviceByPlace(@RequestParam("placeId") int placeId){
        return this.placeService.findDeviceByPlace(placeId);
    }

    /**
     * 根据场地id查询所有设备分页
     * @param placeId
     * @return
     */
    @GetMapping("/place/findDeviceBypId")
    public String findDeviceBypId(@RequestParam("placeId") int placeId){
        return this.placeService.findDeviceBypId(placeId);
    }

    /**
     * 不分页查询第一级场地数据
     */
    @GetMapping("/place/findAllPlaceFirst")
    public @ResponseBody
    List<PlaceEntity> findAllPlaceFirst(){
        return this.placeService.findAllPlaceFirst();
    }

    /**
     * 根据场地id查询所有设备(设备控制)
     * @param placeId
     * @return
     */
    @GetMapping("/place/findDevicesByPlaceId")
    public @ResponseBody
    String findDevicesByPlaceId(@RequestParam("placeId") int placeId){
        return this.placeService.findDeviceByPlace(placeId);
    }

    /**
     * 单文件上传
     *
     * @param file
     * @param request
     * @return
     */
    @PostMapping("/file/upload")
    @ResponseBody
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();
            String filePath = request.getSession().getServletContext().getRealPath("/fileUpload/");

            FileUtil2.uploadFile(file.getBytes(), filePath, fileName);

            result.put("uploadpath", filePath+fileName);
            result.put("fileName",fileName);
            result.put("success", true);
            return result;
    }

    /**
     * 后台添加图片路径
     */
    @PostMapping(value = "/placeMgr/saveFileToDB")
    public @ResponseBody
    void saveFileToDB(int placeId,String uploadpath,String fileName){
        this.placeService.saveFileToDB(placeId,uploadpath,fileName);
    }

    /**
     * 下载文件
     */
    @GetMapping("/file/download")
    public void download(@RequestParam("placeId") int placeId,@RequestParam("type")int type, HttpServletResponse response) throws Exception {
//        //当前是从该工程的WEB-INF//File//下获取文件(该目录可以在下面一行代码配置)然后下载到C:\\users\\downloads即本机的默认下载的目录
//             String realPath = request.getServletContext().getRealPath("//fileUpload//");
//            File file = new File(realPath, fileName);
        PlaceEntity placeEntity = this.placeService.findPlaceById(placeId);
        String fileUrl = placeEntity.getFile();
        String fileName = placeEntity.getFileName();

        File file = new File(fileUrl);
        if (file.exists()) {
//            response.setContentType("application/octet-stream");//如果设置了octet-stream，一定会弹框下载，必须指定类型
//            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
//            if(fileName.endsWith(".jpg")){
//                response.setContentType("application/x-jpg");
//            }else if(fileName.endsWith(".png")){
//                response.setContentType("application/x-png");
//            }else{
//                response.setContentType("image/jpeg");//只有设置jpeg才能预览图片，图片类型必须为png
//            }
            if(type==1){
                response.setContentType("application/octet-stream");//如果设置了octet-stream，一定会弹框下载，必须指定类型
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            }else if(type==2){
                response.setContentType("image/jpeg");//只有设置jpeg才能预览图片，图片类型必须为png,jpg只显示1/3
                response.setHeader("Content-Disposition", "inline;filename=" + fileName);
            }

            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");

            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    bos.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                bos.flush();
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }

}
