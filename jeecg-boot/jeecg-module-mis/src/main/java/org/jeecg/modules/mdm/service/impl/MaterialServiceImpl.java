package org.jeecg.modules.mdm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.mdm.entity.Item;
import org.jeecg.modules.mdm.entity.Material;
import org.jeecg.modules.mdm.mapper.MaterialMapper;
import org.jeecg.modules.mdm.service.IItemService;
import org.jeecg.modules.mdm.service.IMaterialService;
import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.service.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import static org.jeecg.modules.mdm.constant.ItemTypeConstants.MATERIAL;

/**
 * @Description: 物料表
 * @Author: jeecg-boot
 * @Date:   2026-02-03
 * @Version: V1.0
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements IMaterialService {
    @Autowired
    private IStockService stockService;
	@Override
	public void addMaterial(Material material) {
        // 1. 校验编码唯一性（只查未删除的）
        validateCodeUnique(material.getMaterialCode(), null);
	   //新增时设置hasChild为0
	    material.setHasChild(IMaterialService.NOCHILD);
		if(oConvertUtils.isEmpty(material.getPid())){
			material.setPid(IMaterialService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			Material parent = baseMapper.selectById(material.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(material);
//        if(material.getMaterialCode().length() > 5){
//            Item item = buildItemFromMaterial(material);
//            itemService.save(item);
//        }
	}
	
	@Override
	public void updateMaterial(Material material) {
		Material entity = this.getById(material.getId());
		if(entity==null) {
			throw new JeecgBootException("未找到对应实体");
		}
        // 编码变更时：先检查是否被使用，再校验唯一性
        if (!entity.getMaterialCode().equals(material.getMaterialCode())) {
            // 叶子节点且被使用了 → 编码不能改
            if (!hasChildren(material.getId())) {
                checkMaterialInUse(material.getId(), "该物料已被使用，编码不允许修改");
            }
            // 没被使用 → 校验新编码唯一性
            validateCodeUnique(material.getMaterialCode(), material.getId());
        }

		String old_pid = entity.getPid();
		String new_pid = material.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				material.setPid(IMaterialService.ROOT_PID_VALUE);
			}
			if(!IMaterialService.ROOT_PID_VALUE.equals(material.getPid())) {
				baseMapper.updateTreeNodeStatus(material.getPid(), IMaterialService.HASCHILD);
			}
		}
		baseMapper.updateById(material);
//        String itemId = itemService.GetIdByMaterialId(material.getId());
//        if(StrUtil.isNotBlank(itemId)){
//            Item item = buildItemFromMaterial(material);
//            item.setId(itemId);
//            itemService.updateById(item);
//        }
	}
    @Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteMaterial(String id) throws JeecgBootException {
        Material material = this.getById(id);
        if (material == null) {
            throw new JeecgBootException("未找到对应实体");
        }

        // 1. 有子节点 → 父节点，不能删
        if (hasChildren(id)) {
            throw new JeecgBootException("该物料存在下级节点，请先删除下级后再删除");
        }

        // 2. 叶子节点 → 检查业务引用
        checkMaterialInUse(id, "该物料已被使用，无法删除");


        // ===== 3. 执行删除 =====
        updateOldParentNode(material.getPid());
        baseMapper.deleteById(id);
    }
	
//	@Override
//	@Transactional(rollbackFor = Exception.class)
//	public void deleteMaterial(String id) throws JeecgBootException {
//        // 检查是否被使用
//        checkMaterialInUse(id, "该物料已被使用，无法删除");
//		//查询选中节点下所有子节点一并删除
//        id = this.queryTreeChildIds(id);
//        if(id.indexOf(",")>0) {
//            StringBuffer sb = new StringBuffer();
//            String[] idArr = id.split(",");
//            for (String idVal : idArr) {
//                if(idVal != null){
//                    Material material = this.getById(idVal);
//
//                    checkMaterialInUse(idVal, "物料【" + material.getMaterialCode() + "】已被使用，无法删除");
//
//                    String pidVal = material.getPid();
//                    //查询此节点上一级是否还有其他子节点
//                    List<Material> dataList = baseMapper.selectList(new QueryWrapper<Material>().eq("pid", pidVal).notIn("id",Arrays.asList(idArr)));
//                    boolean flag = (dataList == null || dataList.size() == 0) && !Arrays.asList(idArr).contains(pidVal) && !sb.toString().contains(pidVal);
//                    if(flag){
//                        //如果当前节点原本有子节点 现在木有了，更新状态
//                        sb.append(pidVal).append(",");
//                    }
//                }
//            }
//            //批量删除节点
//            baseMapper.deleteBatchIds(Arrays.asList(idArr));
//            //修改已无子节点的标识
//            String[] pidArr = sb.toString().split(",");
//            for(String pid : pidArr){
//                this.updateOldParentNode(pid);
//            }
//        }else{
//            Material material = this.getById(id);
//            if(material==null) {
//                throw new JeecgBootException("未找到对应实体");
//            }
//            updateOldParentNode(material.getPid());
//            baseMapper.deleteById(id);
//
//            String itemId = itemService.GetIdByMaterialId(id);
//            if(StrUtil.isNotBlank(itemId)){
//                itemService.removeById(itemId);
//            }
//        }
//	}
	
	@Override
    public List<Material> queryTreeListNoPage(QueryWrapper<Material> queryWrapper) {
        List<Material> dataList = baseMapper.selectList(queryWrapper);
        List<Material> mapList = new ArrayList<>();
        for(Material data : dataList){
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if(pidVal != null && !IMaterialService.NOCHILD.equals(pidVal)){
                Material rootVal = this.getTreeRoot(pidVal);
                if(rootVal != null && !mapList.contains(rootVal)){
                    mapList.add(rootVal);
                }
            }else{
                if(!mapList.contains(data)){
                    mapList.add(data);
                }
            }
        }
        return mapList;
    }

    @Override
    public List<SelectTreeModel> queryListByCode(String parentCode) {
        String pid = ROOT_PID_VALUE;
        if (oConvertUtils.isNotEmpty(parentCode)) {
            LambdaQueryWrapper<Material> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Material::getPid, parentCode);
            List<Material> list = baseMapper.selectList(queryWrapper);
            if (list == null || list.size() == 0) {
                throw new JeecgBootException("该编码【" + parentCode + "】不存在，请核实!");
            }
            if (list.size() > 1) {
                throw new JeecgBootException("该编码【" + parentCode + "】存在多个，请核实!");
            }
            pid = list.get(0).getId();
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public List<SelectTreeModel> queryListByPid(String pid) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public Item buildItemFromMaterial(Material material) {
        Item item = new Item();
        item.setItemId(material.getId());
        item.setCode(material.getMaterialCode());
        item.setName(material.getMaterialName());
        item.setItemType(MATERIAL);
        item.setSpec(material.getMaterialSpec());
        item.setDelFlag(material.getDelFlag());
        item.setIsActive(material.getStatus());
        item.setIsPackage(material.getIsPackage());
        item.setPackageCapacity(material.getPackageCapacity());
        item.setPackageCapacityUnit(material.getPackageCapacityUnit());
        item.setSysOrgCode(material.getSysOrgCode());
        return item;
    }
	/**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!IMaterialService.ROOT_PID_VALUE.equals(pid)) {
			Long count = baseMapper.selectCount(new QueryWrapper<Material>()
                    .eq("pid", pid)
                    .eq("del_flag", "0"));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IMaterialService.NOCHILD);
			}
		}
	}

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private Material getTreeRoot(String pidVal){
        Material data =  baseMapper.selectById(pidVal);
        if(data != null && !IMaterialService.ROOT_PID_VALUE.equals(data.getPid())){
            return this.getTreeRoot(data.getPid());
        }else{
            return data;
        }
    }

    /**
     * 根据id查询所有子节点id
     * @param ids
     * @return
     */
    private String queryTreeChildIds(String ids) {
        //获取id数组
        String[] idArr = ids.split(",");
        StringBuffer sb = new StringBuffer();
        for (String pidVal : idArr) {
            if(pidVal != null){
                if(!sb.toString().contains(pidVal)){
                    if(sb.toString().length() > 0){
                        sb.append(",");
                    }
                    sb.append(pidVal);
                    this.getTreeChildIds(pidVal,sb);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 递归查询所有子节点
     * @param pidVal
     * @param sb
     * @return
     */
    private StringBuffer getTreeChildIds(String pidVal,StringBuffer sb){
        List<Material> dataList = baseMapper.selectList(new QueryWrapper<Material>().eq("pid", pidVal));
        if(dataList != null && dataList.size()>0){
            for(Material tree : dataList) {
                if(!sb.toString().contains(tree.getId())){
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(),sb);
            }
        }
        return sb;
    }

    /**
     * 校验编码唯一性
     */
    private void validateCodeUnique(String code, String excludeId) {
        if (StrUtil.isBlank(code)) {
            throw new JeecgBootException("物料编码不能为空");
        }

        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getMaterialCode, code)
                .eq(Material::getDelFlag,"0");

        if (StrUtil.isNotBlank(excludeId)) {
            wrapper.ne(Material::getId, excludeId);
        }

        long count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new JeecgBootException("物料编码【" + code + "】已存在");
        }
    }

    /**
     * 检查物料是否被使用
     * 需要根据实际情况扩展：BOM引用、库存引用、采购订单引用等
     */
    private void checkMaterialInUse(String materialId, String errorMsg) {
        // TODO: 根据实际业务扩展以下检查

        // 2. 检查是否被BOM引用（需要添加BOM相关Service）
        // long bomCount = bomService.countByMaterialId(materialId);
        // if (bomCount > 0) throw new JeecgBootException(errorMsg + "（已被BOM引用）");

        // 3. 检查库存中是否存在
        long stockCount = stockService.count(
                new QueryWrapper<Stock>()
                        .eq("goods_id", materialId)
        );
        if (stockCount > 0) {
            throw new JeecgBootException(errorMsg + "（库存中仍存在）");
        }
        // 4. 检查是否有未完成的采购/销售订单
        // ...
    }

    /**
     * 是否有未删除的子节点
     */
    private boolean hasChildren(String id) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getPid, id)
                .eq(Material::getDelFlag, "0");
        return baseMapper.selectCount(wrapper) > 0;
    }
}
