package com.sv.mc.service;

import com.sv.mc.pojo.*;
import com.sv.mc.util.DataSourceResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 接口
 * author:赵政博
 */
public interface BranchService<T> extends BaseService<T>{
    /**1
     * 保存数据
     * @param branch 分公司数据
     * @return       BranchEntity
     */
    BranchEntity save(BranchEntity branch);

    /**2
     * 根据分公司id查询对应分公司数据
     * @param id  分公司id
     * @return BranchEntity
     */
    BranchEntity findBranchById(int id);

    /**
     * 分页查询分公司数据
     */
    String findAllBranchByPage(int page, int pageSize,HttpSession session);

    /**
     * 根据分公司id更改对应的分公司数据
     * @param branch 新分公司名称
     * @return BranchEntity
     */
    BranchEntity updateBranchDataById(BranchEntity branch);

    /**
     * 插入一条分公司数据
     * @param branch
     * @return BranchEntity
     */
    BranchEntity insertBranch(BranchEntity branch);

    /**
     * 根据id修改状态
     */
    void deleteBranch(int branchId);


    /**
     * 查询所有总公司和分公司内容
     */
    String allBranchAndHead();


    /**
     * 查询所有总公司和分公司和代理商内容
     */
    String allBranchAndHeadAndVendor();

    /**
     * 查询所有状态为1的用户
     */
    List<UserEntity> findAllByStatus();

    /**
     * 根据分公司id查询下面的场地
     */
    List<PlaceEntity> findAllPlaceByBranchId(int branchId);

    /**
     * 根据分公司id查询下面的合同
     */
    String findContractsByBranchId(int branchId);

    /**
     * 根据分公司id查询历史合同
     */
    String findHistoryContractByBranchId(int branchId);

    /**
     * 分公司绑定场地
     * @param branchId
     * @param placeId
     */
    void branchBoundPlace(int branchId, int placeId);
}
