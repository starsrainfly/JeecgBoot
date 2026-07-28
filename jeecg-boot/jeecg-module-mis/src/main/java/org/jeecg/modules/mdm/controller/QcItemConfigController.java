package org.jeecg.modules.mdm.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.mdm.entity.QcItemConfig;
import org.jeecg.modules.mdm.service.IQcItemConfigService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 质检项目配置
 * @Author: jeecg-boot
 * @Date:   2026-07-24
 * @Version: V1.0
 */
@Tag(name="质检项目配置")
@RestController
@RequestMapping("/mdm/qcItemConfig")
@Slf4j
public class QcItemConfigController extends JeecgController<QcItemConfig, IQcItemConfigService> {
	@Autowired
	private IQcItemConfigService qcItemConfigService;
	
	/**
	 * 分页列表查询
	 *
	 * @param qcItemConfig
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "质检项目配置-分页列表查询")
	@Operation(summary="质检项目配置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<QcItemConfig>> queryPageList(QcItemConfig qcItemConfig,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<QcItemConfig> queryWrapper = QueryGenerator.initQueryWrapper(qcItemConfig, req.getParameterMap());
		Page<QcItemConfig> page = new Page<QcItemConfig>(pageNo, pageSize);
		IPage<QcItemConfig> pageList = qcItemConfigService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param qcItemConfig
	 * @return
	 */
	@AutoLog(value = "质检项目配置-添加")
	@Operation(summary="质检项目配置-添加")
	@RequiresPermissions("mdm:mis_qc_item_config:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody QcItemConfig qcItemConfig) {
		qcItemConfigService.save(qcItemConfig);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param qcItemConfig
	 * @return
	 */
	@AutoLog(value = "质检项目配置-编辑")
	@Operation(summary="质检项目配置-编辑")
	@RequiresPermissions("mdm:mis_qc_item_config:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody QcItemConfig qcItemConfig) {
		qcItemConfigService.updateById(qcItemConfig);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "质检项目配置-通过id删除")
	@Operation(summary="质检项目配置-通过id删除")
	@RequiresPermissions("mdm:mis_qc_item_config:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		qcItemConfigService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "质检项目配置-批量删除")
	@Operation(summary="质检项目配置-批量删除")
	@RequiresPermissions("mdm:mis_qc_item_config:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.qcItemConfigService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "质检项目配置-通过id查询")
	@Operation(summary="质检项目配置-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<QcItemConfig> queryById(@RequestParam(name="id",required=true) String id) {
		QcItemConfig qcItemConfig = qcItemConfigService.getById(id);
		if(qcItemConfig==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(qcItemConfig);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param qcItemConfig
    */
    @RequiresPermissions("mdm:mis_qc_item_config:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, QcItemConfig qcItemConfig) {
        return super.exportXls(request, qcItemConfig, QcItemConfig.class, "质检项目配置");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("mdm:mis_qc_item_config:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, QcItemConfig.class);
    }

}
