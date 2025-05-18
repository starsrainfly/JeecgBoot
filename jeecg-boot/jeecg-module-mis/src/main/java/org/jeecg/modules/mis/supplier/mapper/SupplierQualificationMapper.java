package org.jeecg.modules.mis.supplier.mapper;

import java.util.List;
import org.jeecg.modules.mis.supplier.entity.SupplierQualification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 供应商质证表
 * @Author: jeecg-boot
 * @Date:   2025-05-18
 * @Version: V1.0
 */
public interface SupplierQualificationMapper extends BaseMapper<SupplierQualification> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId 主表id
   * @return List<SupplierQualification>
   */
	public List<SupplierQualification> selectByMainId(@Param("mainId") String mainId);
}
