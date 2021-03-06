package com.sv.mc.service.impl;

import com.google.gson.Gson;
import com.sv.mc.pojo.*;
import com.sv.mc.repository.*;
import com.sv.mc.service.BusinessService;
import com.sv.mc.service.PlaceService;
import com.sv.mc.util.DataSourceResult;
import com.sv.mc.util.DateJsonValueProcessor;
import com.sv.mc.util.WxUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.jms.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PlaceServiceImpl implements PlaceService {
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private VendorRepository vendorRepository;
    @Resource
    private DeviceRepository deviceRepository;
    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private CityRepository cityRepository;
    @Resource
    private DeviceModelRepository deviceModelRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * 1
     * 保存缓存数据
     *
     * @param place 场地数据
     * @return
     */
    @Override
    public PlaceEntity save(PlaceEntity place) {
        return this.placeRepository.save(place);
    }

    /**
     * 2
     * 查询所有场地数据列表
     *
     * @return List<PranchEntity>
     */
    @Override
    @Transactional
    public List<PlaceEntity> findAllEntities() {
        return this.placeRepository.findAll();
    }

    /**
     * 3
     * 根据场地id查询对应场地数据
     *
     * @param id 分公司id
     * @return PranchEntity
     */
    @Override
    @Transactional
    public PlaceEntity findPlaceById(int id) {
        return this.placeRepository.findPlaceById(id);
    }

    /**
     * 5
     * 根据场地id更改场地数据
     *
     * @param id    场地
     * @param place 场地实体类
     * @return PranchEntity
     */
    @Override
    public PlaceEntity updatePlaceById(int id, PlaceEntity place) {
        return this.placeRepository.save(place);

    }

    @Override
    public List findPlace(int id) {
        return null;
    }


    @Override
    public String findAllPlaceByPage(int page, int pageSize) {
        int offset = ((page - 1) * pageSize);
        List<PlaceEntity> placeEntityList = this.placeRepository.findAllPlaceByPage(offset, pageSize);
        int total = this.placeRepository.findPlaceTotal();

        JsonConfig config = new JsonConfig();
        config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        config.setExcludes(new String[]{"deviceEntities"});//红色的部分是过滤掉deviceEntities对象 不转成JSONArray

        JSONArray jsonArray = JSONArray.fromObject(placeEntityList, config);
        JSONArray jsonArray1 = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        String superiorName = "";
        String levelFlagName = "";
        String userName = "";
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Integer superiorId = Integer.parseInt(jsonObject.get("superiorId").toString());
            int levelFlag = Integer.parseInt(jsonObject.get("levelFlag").toString());
            int businessId = Integer.parseInt(jsonObject.get("businessId").toString());
            int cityId = Integer.parseInt(jsonObject.get("cityId").toString());
            int userId = Integer.parseInt(jsonObject.get("userId").toString());
            if (superiorId != null) {
                if (levelFlag == 1) {
                    levelFlagName = "总部";
                    superiorName = this.vendorRepository.findHeadNameById(superiorId).getName();
                } else if (levelFlag == 2) {
                    levelFlagName = "分公司";
                    superiorName = this.vendorRepository.findBranchNameById(superiorId).getName();
                } else if (levelFlag == 3) {
                    levelFlagName = "代理商";
                    superiorName = this.vendorRepository.findVendorById(superiorId).getName();
                }
            }
            String businessName = this.businessRepository.findBusinessById(businessId).getName();
            String cityName = this.cityRepository.findCityById(cityId).getName();

            userName = this.userRepository.findUserById(userId).getName();
            jsonObject.put("superiorId", superiorId + "_" + superiorName);
            jsonObject.put("userName", userName);
            jsonObject.put("superiorName", superiorName);
            jsonObject.put("levelFlagName", levelFlagName);
            jsonObject.put("businessName", businessName);
            jsonObject.put("cityName", cityName);
            jsonArray1.add(jsonObject);
        }

        jsonObject2.put("data", jsonArray1);
        jsonObject2.put("total", total);
        return jsonObject2.toString();
    }

    @Override
    public String findAllPlace(Map map, HttpSession session) {
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        int superId = userEntity.getGradeId();
        int pId = userEntity.getpId();//隶属单位id

        List<PlaceEntity> placeEntityList;
        if (map.isEmpty()) {
            if (superId == 1) {
                placeEntityList = this.placeRepository.findAllPlaces2();//查询所有场地
            } else if (superId == 2) {
                placeEntityList = this.placeRepository.findAllPlaces3(pId);//查询分公司下的所有场地
            } else if (superId == 3) {
                placeEntityList = this.placeRepository.findAllPlaces4(pId);//查询代理商下的所有场地
            } else {
                placeEntityList = this.placeRepository.findAllPlaces5(pId);//查询场地
            }
        } else {
            int placeId = Integer.parseInt(map.get("id").toString());
            placeEntityList = this.placeRepository.findPlaceByParentId2(placeId);
        }
        JsonConfig config = new JsonConfig();
        config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        config.setExcludes(new String[]{"deviceEntities"});//红色的部分是过滤掉deviceEntities对象 不转成JSONArray

        JSONArray jsonArray = JSONArray.fromObject(placeEntityList, config);
        JSONArray jsonArray1 = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            String superiorName = "";
            String levelFlagName = "";
            String userName = "";
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Integer superiorId = Integer.parseInt(jsonObject.get("superiorId").toString());
            int levelFlag = Integer.parseInt(jsonObject.get("levelFlag").toString());
            int businessId = Integer.parseInt(jsonObject.get("businessId").toString());
            int cityId = Integer.parseInt(jsonObject.get("cityId").toString());
            int userId = Integer.parseInt(jsonObject.get("userId").toString());
            if (superiorId != null) {
                if (levelFlag == 1) {
                    levelFlagName = "总部";
                    superiorName = this.vendorRepository.findHeadNameById(superiorId).getName();
                } else if (levelFlag == 2) {
                    levelFlagName = "分公司";
                    superiorName = this.vendorRepository.findBranchNameById(superiorId).getName();
                } else if (levelFlag == 3) {
                    levelFlagName = "代理商";
                    superiorName = this.vendorRepository.findVendorById(superiorId).getName();
                }
            }
            String businessName = this.businessRepository.findBusinessById(businessId).getName();
            String cityName = this.cityRepository.findCityById(cityId).getName();

            userName = this.userRepository.findUserById(userId).getName();

            jsonObject.put("superiorId", superiorId + "_" + superiorName);
            if (jsonObject.get("superiorId") != null) {
                jsonObject.put("superiorName", superiorName);
            } else {
                jsonObject.put("superiorName", "");
            }

            if (jsonObject.get("levelFlag") != null) {
                jsonObject.put("levelFlagName", levelFlagName);
            } else {
                jsonObject.put("levelFlagName", "");
            }

            jsonObject.put("userName", userName);
            jsonObject.put("businessName", businessName);
            jsonObject.put("cityName", cityName);
            jsonObject.put("hasChildren", true);
            jsonArray1.add(jsonObject);
        }
        return jsonArray1.toString();
    }


    /**
     * 4
     * 插入一条场地数据
     *
     * @param
     */
    @Override
    public PlaceEntity insertPlace(Map map) {
        PlaceEntity placeEntity = new PlaceEntity();
        Object id = map.get("id");
        Object businessId = map.get("businessId");
        Object cityId = map.get("cityId");
        Object endDateTime = map.get("endDateTime");
        Object startDateTime = map.get("startDateTime");
        Object name = map.get("name");
        Object placeAddress = map.get("placeAddress");
        Object placeSn = map.get("placeSn");
        int userId = Integer.parseInt(map.get("userId").toString());
//                Object principal = map.get("principal");
        int superiorId = Integer.parseInt(map.get("superiorId").toString().split("_")[0]);//上级公司id
        String superiorName = map.get("superiorId").toString().split("_")[1];//上级公司name

        placeEntity.setDiscardStatus(1);
        if (id != null) {
            placeEntity.setId(Integer.parseInt(id.toString()));
        }
        placeEntity.setName(name.toString());
//                placeEntity.setPrincipal(principal.toString());
        placeEntity.setPlaceAddress(placeAddress.toString());
        placeEntity.setBusinessId(Integer.parseInt(businessId.toString()));
        placeEntity.setCityId(Integer.parseInt(cityId.toString()));
        placeEntity.setPlaceSn(placeSn.toString());
        placeEntity.setUserId(userId);
        placeEntity.setEndDateTime(Timestamp.valueOf(endDateTime.toString()));
        placeEntity.setStartDateTime(Timestamp.valueOf(startDateTime.toString()));
        placeEntity.setpId(null);

        HeadQuartersEntity headQuartersEntity = this.vendorRepository.findHeadNameByIdAndName(superiorId, superiorName);//根据id查询总部信息
        if (headQuartersEntity == null) {  //如果分公司表中没有查到数据，就查总部表
            BranchEntity branchEntity = this.vendorRepository.findBranchNameByIdAndName(superiorId, superiorName);//根据id查询分公司信息
            if (branchEntity == null) {
                VendorEntity vendorEntity = this.vendorRepository.findVendorById(superiorId);//根据id查询代理商信息
                placeEntity.setLevelFlag(3);
                placeEntity.setSuperiorId(vendorEntity.getId());
            } else {
                placeEntity.setLevelFlag(2);
                placeEntity.setSuperiorId(branchEntity.getId());
            }
        } else {
            placeEntity.setLevelFlag(1);
            placeEntity.setSuperiorId(headQuartersEntity.getId());
        }

        return this.placeRepository.save(placeEntity);
    }


    /**
     * 修改场地数据
     *
     * @param
     * @return
     */
    @Override
    public PlaceEntity updatePlace(Map map) {
        PlaceEntity placeEntity = new PlaceEntity();
        Object id = map.get("id");
        Object businessId = map.get("businessId");
        Object cityId = map.get("cityId");
        Object endDateTime = map.get("endDateTime");
        Object startDateTime = map.get("startDateTime");
        Object name = map.get("name");
        Object placeAddress = map.get("placeAddress");
        Object placeSn = map.get("placeSn");
//                Object file = map.get("file");
//                Object principal = map.get("principal");
        int userId = Integer.parseInt(map.get("userId").toString());
        int superiorId = Integer.parseInt(map.get("superiorId").toString().split("_")[0]);//上级公司id
        String superiorName = map.get("superiorId").toString().split("_")[1];//上级公司name

        placeEntity.setDiscardStatus(1);
        if (id != null) {
            placeEntity.setId(Integer.parseInt(id.toString()));
        }
        placeEntity.setpId(null);
        placeEntity.setName(name.toString());
        placeEntity.setUserId(userId);
        placeEntity.setPlaceAddress(placeAddress.toString());
        placeEntity.setBusinessId(Integer.parseInt(businessId.toString()));
        placeEntity.setCityId(Integer.parseInt(cityId.toString()));
        placeEntity.setPlaceSn(placeSn.toString());
        placeEntity.setEndDateTime(Timestamp.valueOf(endDateTime.toString()));
        placeEntity.setStartDateTime(Timestamp.valueOf(startDateTime.toString()));
//                placeEntity.setFile(file.toString());


        HeadQuartersEntity headQuartersEntity = this.vendorRepository.findHeadNameByIdAndName(superiorId, superiorName);//根据id查询总部信息
        if (headQuartersEntity == null) {  //如果分公司表中没有查到数据，就查总部表
            BranchEntity branchEntity = this.vendorRepository.findBranchNameByIdAndName(superiorId, superiorName);//根据id查询分公司信息
            if (branchEntity == null) {
                VendorEntity vendorEntity = this.vendorRepository.findVendorById(superiorId);//根据id查询代理商信息
                placeEntity.setLevelFlag(3);
                placeEntity.setSuperiorId(vendorEntity.getId());
            } else {
                placeEntity.setLevelFlag(2);
                placeEntity.setSuperiorId(branchEntity.getId());
            }
        } else {
            placeEntity.setLevelFlag(1);
            placeEntity.setSuperiorId(headQuartersEntity.getId());
        }

        return this.placeRepository.save(placeEntity);
    }

    /**
     * 4
     * 插入一条场地数据
     *
     * @param
     */
    @Override
    public PlaceEntity insertPlaceChild(Map map) {
        PlaceEntity placeEntity = new PlaceEntity();
        int pId = Integer.parseInt(map.get("pId").toString());
        Object id = map.get("id");
        Object businessId = map.get("businessId");
        Object cityId = map.get("cityId");
        Object endDateTime = map.get("endDateTime");
        Object startDateTime = map.get("startDateTime");
        Object name = map.get("name");
        Object placeAddress = map.get("placeAddress");
        Object placeSn = map.get("placeSn");
        Object file = map.get("file");
        int userId = Integer.parseInt(map.get("userId").toString());
        int superiorId = Integer.parseInt(map.get("superiorId").toString().split("_")[0]);//上级公司id
        String superiorName = map.get("superiorId").toString().split("_")[1];//上级公司name

        placeEntity.setDiscardStatus(1);
        if (id != null) {
            placeEntity.setId(Integer.parseInt(id.toString()));
        }
        placeEntity.setName(name.toString());
        placeEntity.setPlaceAddress(placeAddress.toString());
        placeEntity.setBusinessId(Integer.parseInt(businessId.toString()));
        placeEntity.setCityId(Integer.parseInt(cityId.toString()));
        placeEntity.setPlaceSn(placeSn.toString());
        placeEntity.setUserId(userId);
        placeEntity.setEndDateTime(Timestamp.valueOf(endDateTime.toString()));
        placeEntity.setStartDateTime(Timestamp.valueOf(startDateTime.toString()));
        placeEntity.setpId(pId);
        placeEntity.setFile(file.toString());

        HeadQuartersEntity headQuartersEntity = this.vendorRepository.findHeadNameByIdAndName(superiorId, superiorName);//根据id查询总部信息
        if (headQuartersEntity == null) {  //如果分公司表中没有查到数据，就查总部表
            BranchEntity branchEntity = this.vendorRepository.findBranchNameByIdAndName(superiorId, superiorName);//根据id查询分公司信息
            if (branchEntity == null) {
                VendorEntity vendorEntity = this.vendorRepository.findVendorById(superiorId);//根据id查询代理商信息
                placeEntity.setLevelFlag(3);
                placeEntity.setSuperiorId(vendorEntity.getId());
            } else {
                placeEntity.setLevelFlag(2);
                placeEntity.setSuperiorId(branchEntity.getId());
            }
        } else {
            placeEntity.setLevelFlag(1);
            placeEntity.setSuperiorId(headQuartersEntity.getId());
        }

        return this.placeRepository.save(placeEntity);
    }

    @Override
    public PlaceEntity updatePlaceChild(Map map) {
        PlaceEntity placeEntity = new PlaceEntity();
        int pId = Integer.parseInt(map.get("pId").toString());
        Object id = map.get("id");
        Object businessId = map.get("businessId");
        Object cityId = map.get("cityId");
        Object endDateTime = map.get("endDateTime");
        Object startDateTime = map.get("startDateTime");
        Object name = map.get("name");
        Object placeAddress = map.get("placeAddress");
        Object placeSn = map.get("placeSn");
//                Object principal = map.get("principal");
        int userId = Integer.parseInt(map.get("userId").toString());
        int superiorId = Integer.parseInt(map.get("superiorId").toString().split("_")[0]);//上级公司id
        String superiorName = map.get("superiorId").toString().split("_")[1];//上级公司name

        placeEntity.setDiscardStatus(1);
        if (id != null) {
            placeEntity.setId(Integer.parseInt(id.toString()));
        }
        placeEntity.setpId(pId);
        placeEntity.setName(name.toString());
        placeEntity.setUserId(userId);
        placeEntity.setPlaceAddress(placeAddress.toString());
        placeEntity.setBusinessId(Integer.parseInt(businessId.toString()));
        placeEntity.setCityId(Integer.parseInt(cityId.toString()));
        placeEntity.setPlaceSn(placeSn.toString());
        placeEntity.setEndDateTime(Timestamp.valueOf(endDateTime.toString()));
        placeEntity.setStartDateTime(Timestamp.valueOf(startDateTime.toString()));


        HeadQuartersEntity headQuartersEntity = this.vendorRepository.findHeadNameByIdAndName(superiorId, superiorName);//根据id查询总部信息
        if (headQuartersEntity == null) {  //如果分公司表中没有查到数据，就查总部表
            BranchEntity branchEntity = this.vendorRepository.findBranchNameByIdAndName(superiorId, superiorName);//根据id查询分公司信息
            if (branchEntity == null) {
                VendorEntity vendorEntity = this.vendorRepository.findVendorById(superiorId);//根据id查询代理商信息
                placeEntity.setLevelFlag(3);
                placeEntity.setSuperiorId(vendorEntity.getId());
            } else {
                placeEntity.setLevelFlag(2);
                placeEntity.setSuperiorId(branchEntity.getId());
            }
        } else {
            placeEntity.setLevelFlag(1);
            placeEntity.setSuperiorId(headQuartersEntity.getId());
        }

        return this.placeRepository.save(placeEntity);
    }

    @Override
    public void deletePlace(int placeId) {
        PlaceEntity placeEntity = findPlaceById(placeId);
        placeEntity.setDiscardStatus(0);
        this.placeRepository.save(placeEntity);

    }

    @Override
    public String findDeviceByPlace(int pId) {
        List<Object[]> deviceEntities = this.placeRepository.findAllChildById(pId);
        List<DeviceEntity> deviceList = new ArrayList<>();
        String model = "";
        String type = "";
        String placeName = "";
        for (int i = 0; i < deviceEntities.size(); i++) {
            Object[] object = deviceEntities.get(i);
            int id = Integer.parseInt(object[0].toString());
            deviceList.add(this.deviceRepository.findDeviceById(id));
        }
        JsonConfig config = new JsonConfig();
        config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
//                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        config.setExcludes(new String[]{"deviceModelEntity", "placeEntity", "supplierEntity", "priceEntities"});
        JSONArray jsonArray = JSONArray.fromObject(deviceList, config);//转化为jsonArray
        JSONArray jsonArray1 = new JSONArray();//新建json数组

        for (int y = 0; y < jsonArray.size(); y++) {
            JSONObject jsonObject2 = jsonArray.getJSONObject(y);
            Object[] object = deviceEntities.get(y);
            int typeId = Integer.parseInt(object[2].toString());
            int placeId = Integer.parseInt(object[1].toString());
            model = this.deviceModelRepository.findById(typeId).getName();
            type = this.deviceModelRepository.findById(typeId).getModel();
            placeName = this.placeRepository.findPlaceById(placeId).getName();
            jsonObject2.put("model", model);
            jsonObject2.put("type", type);
            jsonObject2.put("placeName", placeName);
            jsonArray1.add(jsonObject2);
        }
        return jsonArray1.toString();

    }

    @Override
    public String findDeviceByPlaceId(int placeId, String deviceId, HttpSession session) throws ParseException{
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        int superId = userEntity.getGradeId();
        int pId = userEntity.getpId();//隶属单位id

        WxUtil wxUtil = new WxUtil();

        List<DeviceEntity> deviceEntityList = new ArrayList<>();
        int count = 0;
        if (deviceId.equals("0")) {
            List<Object[]> deviceEntities = this.placeRepository.findAllChildById(placeId);//查询主场地下的所有子场地
            for (int i = 0; i < deviceEntities.size(); i++) {//循环遍历子场地
                Object[] object = deviceEntities.get(i);
                int id = Integer.parseInt(object[0].toString());//获取每个子场地id
                deviceEntityList.add(this.deviceRepository.findDeviceById(id));//根据子场地id查询场地信息，放入list中
            }
            count = this.placeRepository.findAllChildByIdCount(placeId);
        } else {
            Integer device = this.deviceRepository.queryDeviceIdByDeviceCode(deviceId);

            if (superId == 1) {
                deviceEntityList = this.deviceRepository.findDevicesById(device);
                count = this.deviceRepository.findDevicesByIdCount(device);
            } else if (superId == 2) {
                deviceEntityList = this.deviceRepository.findDevicesById2(device, pId);
                count = this.deviceRepository.findDevicesById2Count(device, pId);
            } else if (superId == 3) {
                deviceEntityList = this.deviceRepository.findDevicesById3(device, pId);
                count = this.deviceRepository.findDevicesById3Count(device, pId);
            } else {
                deviceEntityList = this.deviceRepository.findDevicesById4(device, pId);
                count = this.deviceRepository.findDevicesById4Count(device, pId);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        JsonConfig config = new JsonConfig();
        config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        config.setExcludes(new String[]{"priceEntities"});//红色的部分是过滤掉deviceEntities对象 不转成JSONArray

        JSONArray jsonArray = JSONArray.fromObject(deviceEntityList, config);
        JSONArray jsonArray1 = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        for (int i = 0; i < jsonArray.size(); i++) {
            String placeName = "";
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int placeesId = Integer.parseInt(jsonObject.get("placeId").toString());
            int mcIsNotOnline = Integer.parseInt(jsonObject.get("mcIsNotOnline").toString());
            if(mcIsNotOnline==1){
                jsonObject.put("offlineTime", 0);
            }else{
                String time = jsonObject.get("lastCorrespondTime").toString();
//                System.out.println(time);
                Date oldDate = sdf.parse(time);

                Date nowDate = new Date();

                long timeCount = wxUtil.getDifference(oldDate,nowDate,1);//计算
                jsonObject.put("offlineTime", timeCount);
            }

            placeName = this.placeRepository.findPlaceById(placeesId).getName();

            jsonObject.put("placeName", placeName);
            jsonArray1.add(jsonObject);
        }
        jsonObject1.put("data", jsonArray1.toString());
        jsonObject1.put("total", count);

        return jsonObject1.toString();
    }


    @Override
    public String findPlaceByParentId(int placeId) {
        List<PlaceEntity> placeEntityList = this.placeRepository.findPlaceByParentId2(placeId);
        JsonConfig config = new JsonConfig();
        config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        config.setExcludes(new String[]{"deviceEntities"});//红色的部分是过滤掉deviceEntities对象 不转成JSONArray
        JSONArray jsonArray = JSONArray.fromObject(placeEntityList, config);//转化为jsonArray
        JSONArray jsonArray1 = new JSONArray();//新建json数组

        String superiorName = "";
        String levelFlagName = "";
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject12 = jsonArray.getJSONObject(i);
//                        System.out.println(jsonObject12);
            int superiorId = Integer.parseInt(jsonObject12.get("superiorId").toString());
            int levelFlag = Integer.parseInt(jsonObject12.get("levelFlag").toString());
            if (levelFlag == 1) {

                levelFlagName = "总部";
                superiorName = this.vendorRepository.findHeadNameById(superiorId).getName();
            } else if (levelFlag == 2) {
                levelFlagName = "分公司";
                superiorName = this.vendorRepository.findBranchNameById(superiorId).getName();
            } else if (levelFlag == 3) {
                levelFlagName = "代理商";
                superiorName = this.vendorRepository.findVendorById(superiorId).getName();
            }
            jsonObject12.put("superiorName", superiorName);
            jsonObject12.put("levelFlagName", levelFlagName);
            jsonArray1.add(jsonObject12);
        }
        return jsonArray1.toString();
    }


    @Override
    public List<PlaceEntity> findAllPlaces() {
        return this.placeRepository.findAllPlace();
    }


    @Override
    public List<PlaceEntity> getPlace(int cityId) {
        return this.placeRepository.queryPlaceEntitiesByCityId(cityId);
    }


    /**
     * 不分页查询第一级场地数据
     */
    @Override
    public List<PlaceEntity> findAllPlaceFirst(HttpSession session) {
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        int superId = userEntity.getGradeId();
        int pId = userEntity.getpId();//隶属单位id
        List<PlaceEntity> placeEntities;

        if (superId == 1) {
            placeEntities = this.placeRepository.findAllPlaces2();
        } else if (superId == 2) {
            placeEntities = this.placeRepository.findAllPlaces3(pId);
        } else if (superId == 3) {
            placeEntities = this.placeRepository.findAllPlaces4(pId);
        } else {
            placeEntities = this.placeRepository.findAllPlaces5(pId);
        }

        return placeEntities;
    }


    /**
     * 把图片存到数据库中
     *
     * @param placeId
     * @param uploadpath
     */
    @Override
    public void saveFileToDB(int placeId, String uploadpath, String fileName) {
        PlaceEntity placeEntity = this.placeRepository.findPlaceById(placeId);
        placeEntity.setFile(uploadpath);
        placeEntity.setFileName(fileName);
        this.placeRepository.save(placeEntity);
    }

    @Override
    public String findDeviceBypId(int placeId) {

        List<DeviceEntity> deviceEntities = this.deviceRepository.findDevicesByPlaceId(placeId);
        int total = deviceEntities.size();
        String model = "";
        String type = "";
        String placeName = "";
        JsonConfig config = new JsonConfig();
        config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
//                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        config.setExcludes(new String[]{"deviceModelEntity", "placeEntity", "supplierEntity", "priceEntities"});
        JSONArray jsonArray = JSONArray.fromObject(deviceEntities, config);//转化为jsonArray
        JSONArray jsonArray1 = new JSONArray();//新建json数组
        JSONObject jsonObject = new JSONObject();

        for (int y = 0; y < jsonArray.size(); y++) {
            JSONObject jsonObject2 = jsonArray.getJSONObject(y);
            DeviceEntity deviceEntity = deviceEntities.get(y);
            model = deviceEntity.getDeviceModelEntity().getName();
            type = deviceEntity.getDeviceModelEntity().getModel();
            placeName = this.placeRepository.findPlaceById(deviceEntity.getPlaceId()).getName();
            jsonObject2.put("model", model);
            jsonObject2.put("type", type);
            jsonObject2.put("placeName", placeName);
            jsonArray1.add(jsonObject2);
        }
        jsonObject.put("data", jsonArray1);
        jsonObject.put("total", total);
        return jsonObject.toString();
    }

    @Override
    public String findDeviceByUser(HttpServletRequest request) {
        List<PlaceEntity> placeEntityList = new ArrayList<>();
        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        int pId = user.getpId();
        int level = user.getGradeId();
        if (level == 1) {
            placeEntityList = this.placeRepository.findAllPlaces2();
        }
//                else if (level == 2){
//                        List<VendorEntity> vendorEntities = this.vendorRepository.findVendorEntityByPid(pId);
//                        if (vendorEntities.size() != 0)
//                }
        else if (level == 2 || level == 3) {
            placeEntityList = this.placeRepository.findAllPlaceById(pId, level);
        } else if (level == 4) {
            PlaceEntity place = this.placeRepository.findPlaceById(pId);
            placeEntityList = new ArrayList<>();
            placeEntityList.add(place);
        }
        int total = this.placeRepository.findPlaceTotal();

        JsonConfig config = new JsonConfig();
        config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        config.setExcludes(new String[]{"deviceEntities"});//红色的部分是过滤掉deviceEntities对象 不转成JSONArray

        JSONArray jsonArray = JSONArray.fromObject(placeEntityList, config);
        JSONArray jsonArray1 = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        String superiorName = "";
        String levelFlagName = "";
        String userName = "";
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Integer superiorId = Integer.parseInt(jsonObject.get("superiorId").toString());
            int levelFlag = Integer.parseInt(jsonObject.get("levelFlag").toString());
            int businessId = Integer.parseInt(jsonObject.get("businessId").toString());
            int cityId = Integer.parseInt(jsonObject.get("cityId").toString());
            int userId = Integer.parseInt(jsonObject.get("userId").toString());
            if (superiorId != null) {
                if (levelFlag == 1) {
                    levelFlagName = "总部";
                    superiorName = this.vendorRepository.findHeadNameById(superiorId).getName();
                } else if (levelFlag == 2) {
                    levelFlagName = "分公司";
                    superiorName = this.vendorRepository.findBranchNameById(superiorId).getName();
                } else if (levelFlag == 3) {
                    levelFlagName = "代理商";
                    superiorName = this.vendorRepository.findVendorById(superiorId).getName();
                }
            }
            String businessName = this.businessRepository.findBusinessById(businessId).getName();
            String cityName = this.cityRepository.findCityById(cityId).getName();

            userName = this.userRepository.findUserById(userId).getName();
            jsonObject.put("superiorId", superiorId + "_" + superiorName);
            jsonObject.put("userName", userName);
            jsonObject.put("superiorName", superiorName);
            jsonObject.put("levelFlagName", levelFlagName);
            jsonObject.put("businessName", businessName);
            jsonObject.put("cityName", cityName);
            jsonArray1.add(jsonObject);
        }

        jsonObject2.put("data", jsonArray1);
        jsonObject2.put("total", total);
        return jsonObject2.toString();
    }


    /**
     * 4
     * 插入一条场地数据
     *
     * @param
     */
    @Override
    public PlaceEntity insertPlaceTree(Map map) {
        PlaceEntity placeEntity = new PlaceEntity();
        int id = Integer.parseInt(map.get("id").toString());
        int pId = Integer.parseInt(map.get("pId").toString());
        Object businessId = map.get("businessId");
        Object cityId = map.get("cityId");
//                Object endDateTime = map.get("endDateTime");
//                Object startDateTime = map.get("startDateTime");
        Object name = map.get("name");
        Object placeAddress = map.get("placeAddress");
        Object placeSn = map.get("placeSn");
        int userId = Integer.parseInt(map.get("userId").toString());


//                int superiorId = Integer.parseInt(map.get("superiorId").toString().split("_")[0]);//上级公司id
//                String superiorName = map.get("superiorId").toString().split("_")[1];//上级公司name
//
        placeEntity.setDiscardStatus(1);
        if (id != 0) {
            placeEntity.setId(id);
        } else {
            placeEntity.setpId(null);
        }
        if (pId != 0) {
            placeEntity.setpId(pId);
        } else {
            placeEntity.setpId(null);
        }

        placeEntity.setName(name.toString());
        placeEntity.setPlaceAddress(placeAddress.toString());
        placeEntity.setBusinessId(Integer.parseInt(businessId.toString()));
        placeEntity.setCityId(Integer.parseInt(cityId.toString()));
        placeEntity.setPlaceSn(placeSn.toString());
        placeEntity.setUserId(userId);
//                placeEntity.setEndDateTime(Timestamp.valueOf(endDateTime.toString()));
//                placeEntity.setStartDateTime(Timestamp.valueOf(startDateTime.toString()));

//                HeadQuartersEntity headQuartersEntity = this.vendorRepository.findHeadNameByIdAndName(superiorId,superiorName);//根据id查询总部信息
//                if(headQuartersEntity==null){  //如果分公司表中没有查到数据，就查总部表
//                        BranchEntity branchEntity = this.vendorRepository.findBranchNameByIdAndName(superiorId,superiorName);//根据id查询分公司信息
//                        if(branchEntity==null){
//                                VendorEntity vendorEntity = this.vendorRepository.findVendorById(superiorId);//根据id查询代理商信息
//                                placeEntity.setLevelFlag(3);
//                                placeEntity.setSuperiorId(vendorEntity.getId());
//                        }else{
//                                placeEntity.setLevelFlag(2);
//                                placeEntity.setSuperiorId(branchEntity.getId());
//                        }
//                }else{
//                        placeEntity.setLevelFlag(1);
//                        placeEntity.setSuperiorId(headQuartersEntity.getId());
//                }

        return this.placeRepository.save(placeEntity);
    }


    /**
     * 修改场地数据
     *
     * @param
     * @return
     */
    @Override
    public PlaceEntity updatePlaceTree(Map map) {
        PlaceEntity placeEntity = new PlaceEntity();
        int id = Integer.parseInt(map.get("id").toString());
        int pId = Integer.parseInt(map.get("pId").toString());
        Object businessId = map.get("businessId");
        Object cityId = map.get("cityId");
//                Object endDateTime = map.get("endDateTime");
//                Object startDateTime = map.get("startDateTime");
        Object name = map.get("name");
        Object placeAddress = map.get("placeAddress");
        Object placeSn = map.get("placeSn");
//                Object file = map.get("file");
//                Object principal = map.get("principal");
        int userId = Integer.parseInt(map.get("userId").toString());
//                int superiorId = Integer.parseInt(map.get("superiorId").toString().split("_")[0]);//上级公司id
//                String superiorName = map.get("superiorId").toString().split("_")[1];//上级公司name

        placeEntity.setDiscardStatus(1);
        if (id != 0) {
            placeEntity.setId(id);
        } else {
            placeEntity.setpId(null);
        }
        if (pId != 0) {
            placeEntity.setpId(pId);
        } else {
            placeEntity.setpId(null);
        }
        placeEntity.setName(name.toString());
        placeEntity.setUserId(userId);
        placeEntity.setPlaceAddress(placeAddress.toString());
        placeEntity.setBusinessId(Integer.parseInt(businessId.toString()));
        placeEntity.setCityId(Integer.parseInt(cityId.toString()));
        placeEntity.setPlaceSn(placeSn.toString());
//                placeEntity.setEndDateTime(Timestamp.valueOf(endDateTime.toString()));
//                placeEntity.setStartDateTime(Timestamp.valueOf(startDateTime.toString()));
//                placeEntity.setFile(file.toString());


//                HeadQuartersEntity headQuartersEntity = this.vendorRepository.findHeadNameByIdAndName(superiorId,superiorName);//根据id查询总部信息
//                if(headQuartersEntity==null){  //如果分公司表中没有查到数据，就查总部表
//                        BranchEntity branchEntity = this.vendorRepository.findBranchNameByIdAndName(superiorId,superiorName);//根据id查询分公司信息
//                        if(branchEntity==null){
//                                VendorEntity vendorEntity = this.vendorRepository.findVendorById(superiorId);//根据id查询代理商信息
//                                placeEntity.setLevelFlag(3);
//                                placeEntity.setSuperiorId(vendorEntity.getId());
//                        }else{
//                                placeEntity.setLevelFlag(2);
//                                placeEntity.setSuperiorId(branchEntity.getId());
//                        }
//                }else{
//                        placeEntity.setLevelFlag(1);
//                        placeEntity.setSuperiorId(headQuartersEntity.getId());
//                }

        return this.placeRepository.save(placeEntity);
    }


    @Override
    public PlaceEntity getPlaceByName(String name) {
        return this.placeRepository.getPlaceName(name);
    }

    @Override
    public List<PlaceEntity> getPlaceBy_ID(int pid) {
        return this.placeRepository.getPlaceBy_ID(pid);
    }


    /**
     * 查询所有未删除场地
     *
     * @return
     */
    @Override
    public List<PlaceEntity> allPlaceUnDelete() {
        return this.placeRepository.findAllPlace();
    }
}
