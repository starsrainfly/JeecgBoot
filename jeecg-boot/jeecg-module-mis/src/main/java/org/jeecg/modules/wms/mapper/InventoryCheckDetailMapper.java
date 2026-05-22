package org.jeecg.modules.wms.mapper;

import java.util.List;
import org.jeecg.modules.wms.entity.InventoryCheckDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 盘库明细表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
public interface InventoryCheckDetailMapper extends BaseMapper<InventoryCheckDetail> {

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
    * @return List<InventoryCheckDetail>
    */
	public List<InventoryCheckDetail> selectByMainId(@Param("mainId") String mainId);

}
