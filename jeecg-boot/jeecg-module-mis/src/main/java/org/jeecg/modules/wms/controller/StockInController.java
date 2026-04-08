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

import cn.hutool.core.date.DateTime;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.common.enums.ApproveStatusEnum;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.enums.StockEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.wms.entity.Warehouse;
import org.jeecg.modules.wms.service.IStockService;
import org.jeecg.modules.wms.service.IWarehouseService;
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
import org.jeecg.modules.wms.entity.StockInDetail;
import org.jeecg.modules.wms.entity.StockIn;
import org.jeecg.modules.wms.vo.StockInPage;
import org.jeecg.modules.wms.service.IStockInService;
import org.jeecg.modules.wms.service.IStockInDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
 * @Description: 入库表
 * @Author: jeecg-boot
 * @Date:   2026-04-03
 * @Version: V1.0
 */
@Tag(name="入库表")
@RestController
@RequestMapping("/wms/stockIn")
@Slf4j
public class StockInController {
	@Autowired
	private IStockInService stockInService;
	@Autowired
	private IStockInDetailService stockInDetailService;
	 @Autowired
	 private ISerialNoService serialNoService;
	 @Autowired
	 private ISysUserService userService;
	 @Autowired
	 private IWarehouseService warehouseService;
	/**
	 * 分页列表查询
	 *
	 * @param stockIn
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "入库表-分页列表查询")
	@Operation(summary="入库表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<StockIn>> queryPageList(StockIn stockIn,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("stockInType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("warehouseId", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<StockIn> queryWrapper = QueryGenerator.initQueryWrapper(stockIn, req.getParameterMap(),customeRuleMap);
		Page<StockIn> page = new Page<StockIn>(pageNo, pageSize);
		IPage<StockIn> pageList = stockInService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 分页列表查询
	  *暂时未启用
	  * @param stockIn
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 //@AutoLog(value = "入库表-分页列表查询")
	 @Operation(summary="入库表-分页列表查询")
	 @GetMapping(value = "/productList")
	 public Result<IPage<StockIn>> queryPageProductList(StockIn stockIn,
												 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												 HttpServletRequest req) {
		 // 自定义查询规则
		 Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
		 // 自定义多选的查询规则为：LIKE_WITH_OR
		 customeRuleMap.put("stockInType", QueryRuleEnum.LIKE_WITH_OR);
		 customeRuleMap.put("warehouseId", QueryRuleEnum.LIKE_WITH_OR);

		 QueryWrapper<StockIn> queryWrapper = QueryGenerator.initQueryWrapper(stockIn, req.getParameterMap(),customeRuleMap);
		 Page<StockIn> page = new Page<StockIn>(pageNo, pageSize);
		 IPage<StockIn> pageList = stockInService.page(page, queryWrapper);
		 return Result.OK(pageList);
	 }
	
	/**
	 *   添加
	 *
	 * @param stockInPage
	 * @return
	 */
	@AutoLog(value = "入库表-添加")
	@Operation(summary="入库表-添加")
    @RequiresPermissions("wms:mis_stock_in:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody StockInPage stockInPage) {
		StockIn stockIn = new StockIn();
		BeanUtils.copyProperties(stockInPage, stockIn);
		stockIn.setStockInNo(serialNoService.generateSerialNo(SerialNoPrefixEnum.STOCK_IN.getPrefix()));
		stockIn.setApplyTime(new DateTime());

		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		stockIn.setOperatorUserId(loginUser.getId());  // 记录实际执行人
		stockIn.setOperatorName(loginUser.getRealname());
		if(StringUtils.isNotBlank(stockIn.getPurchaserId())){
			SysUser sysUser = userService.getById(stockIn.getPurchaserId());
			if(sysUser != null){
				stockIn.setPurchaserName(sysUser.getRealname());
			}
		}
		if(StringUtils.isNotBlank(stockIn.getWarehouseId())){
			Warehouse warehouse =  warehouseService.getById(stockIn.getWarehouseId());
			if(warehouse != null){
				stockIn.setWarehouseName(warehouse.getName());
			}
		}

		stockInService.saveMain(stockIn, stockInPage.getStockInDetailList());
		return Result.OK("添加成功！");
	}

	 @Transactional(rollbackFor = Exception.class)
	 @AutoLog(value = "入库表-审核")
	 @Operation(summary="入库表-审核")
	 @RequestMapping(value = "/approve", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> approve(@RequestBody StockInPage stockInPage) {
		 LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		stockInService.approveStockIn(stockInPage,loginUser);
		return Result.OK("审核成功!");
	}
	/**
	 *  编辑
	 *
	 * @param stockInPage
	 * @return
	 */
	@AutoLog(value = "入库表-编辑")
	@Operation(summary="入库表-编辑")
    @RequiresPermissions("wms:mis_stock_in:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody StockInPage stockInPage) {
		StockIn stockIn = new StockIn();
		BeanUtils.copyProperties(stockInPage, stockIn);
		StockIn stockInEntity = stockInService.getById(stockIn.getId());
		if(stockInEntity==null) {
			return Result.error("未找到对应数据");
		}
		stockInService.updateMain(stockIn, stockInPage.getStockInDetailList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "入库表-通过id删除")
	@Operation(summary="入库表-通过id删除")
    @RequiresPermissions("wms:mis_stock_in:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		stockInService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "入库表-批量删除")
	@Operation(summary="入库表-批量删除")
    @RequiresPermissions("wms:mis_stock_in:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.stockInService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "入库表-通过id查询")
	@Operation(summary="入库表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<StockIn> queryById(@RequestParam(name="id",required=true) String id) {
		StockIn stockIn = stockInService.getById(id);
		if(stockIn==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(stockIn);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "入库明细表通过主表ID查询")
	@Operation(summary="入库明细表主表ID查询")
	@GetMapping(value = "/queryStockInDetailByMainId")
	public Result<List<StockInDetail>> queryStockInDetailListByMainId(@RequestParam(name="id",required=true) String id) {
		List<StockInDetail> stockInDetailList = stockInDetailService.selectByMainId(id);
		return Result.OK(stockInDetailList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param stockIn
    */
    @RequiresPermissions("wms:mis_stock_in:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StockIn stockIn) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<StockIn> queryWrapper = QueryGenerator.initQueryWrapper(stockIn, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<StockIn> stockInList = stockInService.list(queryWrapper);

      // Step.3 组装pageList
      List<StockInPage> pageList = new ArrayList<StockInPage>();
      for (StockIn main : stockInList) {
          StockInPage vo = new StockInPage();
          BeanUtils.copyProperties(main, vo);
          List<StockInDetail> stockInDetailList = stockInDetailService.selectByMainId(main.getId());
          vo.setStockInDetailList(stockInDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "入库表列表");
      mv.addObject(NormalExcelConstants.CLASS, StockInPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("入库表数据", "导出人:"+sysUser.getRealname(), "入库表"));
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
    @RequiresPermissions("wms:mis_stock_in:importExcel")
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
              List<StockInPage> list = ExcelImportUtil.importExcel(file.getInputStream(), StockInPage.class, params);
              for (StockInPage page : list) {
                  StockIn po = new StockIn();
                  BeanUtils.copyProperties(page, po);
                  stockInService.saveMain(po, page.getStockInDetailList());
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
