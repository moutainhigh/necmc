package com.sv.mc.service.impl;

import com.google.gson.Gson;
import com.sv.mc.pojo.*;
import com.sv.mc.repository.*;
import com.sv.mc.service.PlaceService;
import com.sv.mc.service.VendorService;
import com.sv.mc.util.DataSourceResult;
import com.sv.mc.util.DateJsonValueProcessor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Author:赵政博
 * 业务层
 */
@Service
public class VendorServiceImpl implements VendorService {
        @Autowired
        private VendorRepository vendorRepository;
        @Autowired
        private HeadQuartersRepository headQuartersRepository;
        @Autowired
        private BranchRepository branchRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private PlaceRepository placeRepository;
        @Autowired
        private ContractRepository contractRepository;

        /**
         * 提交数据
         * @param vendor 代理商数据
         * @return VendorEntity
         */
        @Override
        public VendorEntity save(VendorEntity vendor) {
                return vendorRepository.save(vendor);
        }

        /**
         * 根据id查询代理商
         * @param id  代理商id
         * @return VendorEntity
         */
        @Override
        public VendorEntity findVendorById(int id) {
                return vendorRepository.findVendorById(id);
        }

        /**
         * 根据代理商id更改数据
         * @param id  代理商id
         * @param vendor 代理商数据
         * @return VendorEntity
         */
        @Override
        public VendorEntity updateVendorDataById(int id, VendorEntity vendor) {
                return vendorRepository.save(vendor);
        }

        /**
         * 查询所有代理商数据
         * @return List<VendorEntity>
         */
        @Override
        public List<VendorEntity> findAllEntities() {
                return vendorRepository.findAll();
        }


        /**
         * 分页查询全部数据
         * @param page
         * @param pageSize
         * @return
         */
        @Override
        public String findAllVendorByPage(int page, int pageSize) {
                int offset = ((page-1)*pageSize);
                List<VendorEntity> vendorEntities = this.vendorRepository.findAllVendorByPage(offset,pageSize);

                int total = this.vendorRepository.findVendorTotal();

                JSONArray jsonArray = JSONArray.fromObject(vendorEntities);
                JSONArray jsonArray1 = new JSONArray();
                JSONObject jsonObject2 = new JSONObject();

                String superiorName="";
                String levelFlagName="";
                String userName = "";
                for (int i = 0; i <jsonArray.size() ; i++) {
                        JSONObject jsonObject =jsonArray.getJSONObject(i);
                        int superiorId =Integer.parseInt(jsonObject.get("superiorId").toString());
                        int levelFlag =Integer.parseInt(jsonObject.get("levelFlag").toString());
                        int userId = Integer.parseInt(jsonObject.get("userId").toString());
                        if(levelFlag==1){
                                levelFlagName = "总部";
                                superiorName = this.vendorRepository.findHeadNameById(superiorId).getName();
                        }else if(levelFlag==2){
                                levelFlagName = "分公司";
                                superiorName = this.vendorRepository.findBranchNameById(superiorId).getName();
                        }
                        userName = this.userRepository.findUserById(userId).getName();
                        jsonObject.put("superiorId",superiorId+"_"+superiorName);
                        jsonObject.put("superiorName",superiorName);
                        jsonObject.put("levelFlagName",levelFlagName);
//                        jsonObject.put("userId",userId+"_"+userName);
                        jsonObject.put("userName",userName);
                        jsonArray1.add(jsonObject);
                }


                jsonObject2.put("data",jsonArray1);
                jsonObject2.put("total",total);

                return jsonObject2.toString();
        }

        /**
         * 插入一条代理商数据
         * @param
         * @return VendorEntity
         */
        @Override
        public VendorEntity insertVendor(Map map) {
                return saveData(map);
        }

        /**
         * 修改数据
         * @param map
         * @return
         */
        @Override
        public VendorEntity updateVendorDataById(Map map) {
                return saveData(map);
        }

        /**
         * 删除数据
         * @param headId
         */
        @Override
        public void deleteVendor(int headId) {
                VendorEntity vendorEntity = findVendorById(headId);
                vendorEntity.setDiscardStatus(0);
                this.vendorRepository.save(vendorEntity);
        }


        /**
         * 新增修改通用方法
         */
        private VendorEntity saveData(Map map) {
                VendorEntity vendorEntity = new VendorEntity();
                Object id = map.get("id");
//                Object discardStatus = map.get("discardStatus");
                Object email = map.get("email");
                Object name = map.get("name");
//                Object principal = map.get("principal");
                Object telephone = map.get("telephone");
                Object vendorAddress = map.get("vendorAddress");
                int userId = Integer.parseInt(map.get("userId").toString());
                int superiorId = Integer.parseInt(map.get("superiorId").toString().split("_")[0]);//上级公司id
                String superiorName = map.get("superiorId").toString().split("_")[1];//上级公司name

                vendorEntity.setDiscardStatus(1);
                vendorEntity.setEmail(email.toString());
                if(id!=null){
                        vendorEntity.setId(Integer.parseInt(id.toString()));
                }
                vendorEntity.setName(name.toString());
//                vendorEntity.setPrincipal(principal.toString());\
                vendorEntity.setUserId(userId);
                vendorEntity.setTelephone(telephone.toString());
                vendorEntity.setVendorAddress(vendorAddress.toString());

                HeadQuartersEntity headQuartersEntity = this.vendorRepository.findHeadNameByIdAndName(superiorId,superiorName);//根据id查询总部信息
                if(headQuartersEntity==null){  //如果分公司表中没有查到数据，就查总部表
                        BranchEntity branchEntity = this.vendorRepository.findBranchNameByIdAndName(superiorId,superiorName);//根据id查询分公司信息
                        vendorEntity.setLevelFlag(2);
                        vendorEntity.setSuperiorId(branchEntity.getId());
                }else{
                        vendorEntity.setLevelFlag(1);
                        vendorEntity.setSuperiorId(headQuartersEntity.getId());
                }

                return this.vendorRepository.save(vendorEntity);
        }



        /**
         * 根据代理商id查询下面的场地
         */
        @Override
        public List<PlaceEntity> findAllPlaceByVendorId(int vendorId) {
                return this.vendorRepository.findAllPlaceByVendorId(vendorId);
        }

        /**
         * 代理商绑定场地
         */
        @Override
        public void vendorBoundPlace(int vendorId, int placeId) {
                List<Integer> list = this.placeRepository.findAllPlaceChildById(placeId);//查出placeId下的所有场地id

                ContractEntity contractEntity=new ContractEntity();
                contractEntity.setOwner(vendorId);//甲方
                contractEntity.setSecond(placeId);//乙方
                contractEntity.setFlag(3);//代理商签约
                contractEntity.setUseFlag(1);//使用中
                contractEntity.setPlaceId(placeId);
                this.contractRepository.save(contractEntity);

                for (int i = 0; i <list.size() ; i++) {
                        Integer placeChildId = list.get(i);
                        PlaceEntity placeEntity = this.placeRepository.findPlaceById(placeChildId);
                        placeEntity.setLevelFlag(3);
                        placeEntity.setSuperiorId(vendorId);
                        this.placeRepository.save(placeEntity);
                }
        }

        /**
         * 根据代理商id查询下面的合同
         */
        @Override
        public String findContractByVendorId(int vendorId) {
                List<ContractEntity> list = this.contractRepository.findContractsByVendorId(vendorId);
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));

                JSONArray jsonArray = JSONArray.fromObject(list,config);
                JSONArray jsonArray1 = new JSONArray();
                String placeName = "";
                String fileUrl = "";
                String fileName = "";
                for (int i = 0; i <jsonArray.size() ; i++) {
                        JSONObject jsonObject =jsonArray.getJSONObject(i);
                        int placeId = Integer.parseInt(jsonObject.get("placeId").toString());

                        PlaceEntity placeEntity = this.placeRepository.findPlaceById(placeId);
                        placeName=placeEntity.getName();
                        fileUrl = placeEntity.getFile();
                        fileName=placeEntity.getFileName();
                        jsonObject.put("placeName",placeName);
                        jsonObject.put("fileUrl",fileUrl);
                        jsonObject.put("fileName",fileName);
                        jsonArray1.add(jsonObject);
                }

                return jsonArray1.toString();
        }

        /**
         * 根据代理商id查询合同历史
         */
        @Override
        public String findHistoryContractByVendorId(int vendorId) {
                List<ContractEntity> list = this.contractRepository.findHistoryContractByVendorId(vendorId);
                JsonConfig config = new JsonConfig();
                config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));

                JSONArray jsonArray = JSONArray.fromObject(list,config);
                JSONArray jsonArray1 = new JSONArray();
                String placeName = "";
                String fileUrl = "";
                String fileName = "";
                for (int i = 0; i <jsonArray.size() ; i++) {
                        JSONObject jsonObject =jsonArray.getJSONObject(i);
                        int placeId = Integer.parseInt(jsonObject.get("placeId").toString());

                        PlaceEntity placeEntity = this.placeRepository.findPlaceById(placeId);
                        placeName=placeEntity.getName();
                        fileUrl = placeEntity.getFile();
                        fileName=placeEntity.getFileName();
                        jsonObject.put("placeName",placeName);
                        jsonObject.put("fileUrl",fileUrl);
                        jsonObject.put("fileName",fileName);
                        jsonArray1.add(jsonObject);
                }

                return jsonArray1.toString();
        }
}
