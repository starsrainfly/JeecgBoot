package org.jeecg.modules.wms.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wms.entity.InventoryAdjustDetail;
import org.jeecg.modules.wms.entity.InventoryAdjust;
import org.jeecg.modules.wms.vo.InventoryAdjustPage;
import org.jeecg.modules.wms.service.IInventoryAdjustService;
import org.jeecg.modules.wms.service.IInventoryAdjustDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;


 /**
 * @Description: 盘点调整单主表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
@Tag(name="盘点调整单主表")
@RestController
@RequestMapping("/wms/inventoryAdjust")
@Slf4j
public class InventoryAdjustController {
	@Autowired
	private IInventoryAdjustService inventoryAdjustService;
	@Autowired
	private IInventoryAdjustDetailService inventoryAdjustDetailService;
	
	/**
	 * 分页列表查询
	 *
	 * @param inventoryAdjust
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "盘点调整单主表-分页列表查询")
	@Operation(summary="盘点调整单主表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<InventoryAdjust>> queryPageList(InventoryAdjust inventoryAdjust,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("warehouseId", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<InventoryAdjust> queryWrapper = QueryGenerator.initQueryWrapper(inventoryAdjust, req.getParameterMap(),customeRuleMap);
		Page<InventoryAdjust> page = new Page<InventoryAdjust>(pageNo, pageSize);
		IPage<InventoryAdjust> pageList = inventoryAdjustService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param inventoryAdjustPage
	 * @return
	 */
	@AutoLog(value = "盘点调整单主表-添加")
	@Operation(summary="盘点调整单主表-添加")
    @RequiresPermissions("wms:wms_inventory_adjust:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody InventoryAdjustPage inventoryAdjustPage) {
		InventoryAdjust inventoryAdjust = new InventoryAdjust();
		BeanUtils.copyProperties(inventoryAdjustPage, inventoryAdjust);
		inventoryAdjustService.saveMain(inventoryAdjust, inventoryAdjustPage.getInventoryAdjustDetailList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param inventoryAdjustPage
	 * @return
	 */
	@AutoLog(value = "盘点调整单主表-编辑")
	@Operation(summary="盘点调整单主表-编辑")
    @RequiresPermissions("wms:wms_inventory_adjust:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody InventoryAdjustPage inventoryAdjustPage) {
		InventoryAdjust inventoryAdjust = new InventoryAdjust();
		BeanUtils.copyProperties(inventoryAdjustPage, inventoryAdjust);
		InventoryAdjust inventoryAdjustEntity = inventoryAdjustService.getById(inventoryAdjust.getId());
		if(inventoryAdjustEntity==null) {
			return Result.error("未找到对应数据");
		}
		inventoryAdjustService.updateMain(inventoryAdjust, inventoryAdjustPage.getInventoryAdjustDetailList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "盘点调整单主表-通过id删除")
	@Operation(summary="盘点调整单主表-通过id删除")
    @RequiresPermissions("wms:wms_inventory_adjust:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		inventoryAdjustService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "盘点调整单主表-批量删除")
	@Operation(summary="盘点调整单主表-批量删除")
    @RequiresPermissions("wms:wms_inventory_adjust:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.inventoryAdjustService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "盘点调整单主表-通过id查询")
	@Operation(summary="盘点调整单主表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<InventoryAdjust> queryById(@RequestParam(name="id",required=true) String id) {
		InventoryAdjust inventoryAdjust = inventoryAdjustService.getById(id);
		if(inventoryAdjust==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(inventoryAdjust);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "盘库调整单明细表通过主表ID查询")
	@Operation(summary="盘库调整单明细表主表ID查询")
	@GetMapping(value = "/queryInventoryAdjustDetailByMainId")
	public Result<List<InventoryAdjustDetail>> queryInventoryAdjustDetailListByMainId(@RequestParam(name="id",required=true) String id) {
		List<InventoryAdjustDetail> inventoryAdjustDetailList = inventoryAdjustDetailService.selectByMainId(id);
		return Result.OK(inventoryAdjustDetailList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param inventoryAdjust
    */
    @RequiresPermissions("wms:wms_inventory_adjust:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, InventoryAdjust inventoryAdjust) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<InventoryAdjust> queryWrapper = QueryGenerator.initQueryWrapper(inventoryAdjust, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<InventoryAdjust> inventoryAdjustList = inventoryAdjustService.list(queryWrapper);

      // Step.3 组装pageList
      List<InventoryAdjustPage> pageList = new ArrayList<InventoryAdjustPage>();
      for (InventoryAdjust main : inventoryAdjustList) {
          InventoryAdjustPage vo = new InventoryAdjustPage();
          BeanUtils.copyProperties(main, vo);
          List<InventoryAdjustDetail> inventoryAdjustDetailList = inventoryAdjustDetailService.selectByMainId(main.getId());
          vo.setInventoryAdjustDetailList(inventoryAdjustDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "盘点调整单主表列表");
      mv.addObject(NormalExcelConstants.CLASS, InventoryAdjustPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("盘点调整单主表数据", "导出人:"+sysUser.getRealname(), "盘点调整单主表"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

    /**
    * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("wms:wms_inventory_adjust:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          // 获取上传文件对象
          MultipartFile file = entity.getValue();
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<InventoryAdjustPage> list = ExcelImportUtil.importExcel(file.getInputStream(), InventoryAdjustPage.class, params);
              for (InventoryAdjustPage page : list) {
                  InventoryAdjust po = new InventoryAdjust();
                  BeanUtils.copyProperties(page, po);
                  inventoryAdjustService.saveMain(po, page.getInventoryAdjustDetailList());
              }
              return Result.OK("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.OK("文件导入失败！");
    }

}
