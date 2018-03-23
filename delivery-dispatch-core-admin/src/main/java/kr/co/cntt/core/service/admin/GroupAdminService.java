package kr.co.cntt.core.service.admin;




import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.store.Store;

import java.util.List;

public interface GroupAdminService {

    /**
     * <p> 그룹 목록 조회
     *
     * @param Store
     * @return
     */
    public List<Group> selectGroupsList(Store Store);


    /**
     * <p> 그룹 수정
     *
     * @param group
     * @return
     */
    public int updateGroup(Group group);


    /**
     * <p> 그룹 등록
     *
     * @param group
     * @return
     */
    public int insertGroup(Group group);


    /**
     * <p> 그룹 삭제
     *
     * @param group
     * @return
     */
    public int deleteGroup(Group group);

    /**
     * <p> 서브그룹 수정
     *
     * @param subGroup
     * @return
     */
    public int updateSubGroup(SubGroup subGroup);


    /**
     * <p> 서브그룹 목록 조회
     *
     * @param admin
     * @return
     */
    public List<SubGroup> selectSubGroupsList(Admin admin);

    /**
     * <p> 서브그룹 삭제
     *
     * @param subGroup
     * @return
     */
    public int deleteSubGroup(SubGroup subGroup);


    /**
     * <p> 상점 그룹 목록 조회
     *
     * @param subGroupStoreRel
     * @return
     */
    public List<SubGroupStoreRel> selectSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel);


    /**
     * <p> 서브그룹만 상점 수정
     *
     * @param store
     * @return
     */
    public int updateStoreSubGroup(Store store);


    /**
     * <p> 서브그룹 등록
     *
     * @param subGroup
     * @return
     */
    public int insertSubGroup(SubGroup subGroup);



    /**
     * <p> 상점 미지정 그룹 목록 조회
     *
     * @param subGroupStoreRel
     * @return
     */
    public List<SubGroupStoreRel> selectNoneSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel);

    /**
     * <p> 상점 서브그룹 삭제
     *
     * @param subGroupStoreRel
     * @return
     */
    public int deleteSubGroupStoreRel(SubGroupStoreRel subGroupStoreRel);


    /**
     * <p> 서브그룹에 상점 등록
     *
     * @param store
     * @return
     */
    public int insertSubGroupStoreRel(Store store);


}
