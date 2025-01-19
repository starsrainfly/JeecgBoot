package org.jeecg.modules.mis.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.mis.material.entity.Material;

import java.util.List;
import java.util.Map;

/**
 * @Description: 物料表
 * @Author: jeecg-boot
 * @Date:   2024-11-14
 * @Version: V1.0
 */
public interface MaterialMapper extends BaseMapper<Material> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id, @Param("status") String status);

	/**
	 * 【vue3专用】根据父级ID查询树节点数据
	 *
	 * @param pid
	 * @param query
	 * @return
	 */
	List<SelectTreeModel> queryListByPid(@Param("pid") String pid, @Param("query") Map<String, String> query);

}
