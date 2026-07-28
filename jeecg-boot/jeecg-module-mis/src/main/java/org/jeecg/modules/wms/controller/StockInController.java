package org.jeecg.modules.wms.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateTime;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.common.enums.ApproveStatusEnum;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.enums.StockEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.scm.service.IPurchaseOrderService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.wms.entity.Warehouse;
import org.jeecg.modules.wms.service.IStockService;
import org.jeecg.modules.wms.service.IWarehouseService;
import org.jeecg.modules.wms.vo.StockInDetailVo;
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

	 @Autowired
	 private IPurchaseOrderService purchaseOrderService;
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

		 // ===== 采购到货回写：累加采购明细已入库量，联动采购单状态 =====
		 if ("PURCHASE_ORDER".equals(stockInPage.getSourceOrderType())) {
			 for (StockInDetail d : stockInPage.getStockInDetailList()) {
				 if (oConvertUtils.isNotEmpty(d.getSourceDetailId()) && d.getActualQty() != null) {
					 purchaseOrderService.addReceivedQty(d.getSourceDetailId(), d.getActualQty());
				 }
			 }
		 }

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
		return Result.ok(stockInDetailList);
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

	 /**
	  * 导出
	  * @return
	  */
	 @RequiresPermissions("wms:mis_stock_in:exportXls")
	 @RequestMapping(value = "/exportStockInXls")
	 public ModelAndView exportStockInXls(HttpServletRequest request, StockIn stockIn) {
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
//		 List<StockInPage> pageList = new ArrayList<StockInPage>();
//		 for (StockIn main : stockInList) {
//			 StockInPage vo = new StockInPage();
//			 BeanUtils.copyProperties(main, vo);
//			 List<StockInDetail> stockInDetailList = stockInDetailService.selectByMainId(main.getId());
//			 vo.setStockInDetailList(stockInDetailList);
//			 pageList.add(vo);
//		 }

		 // Step.4 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 mv.addObject(NormalExcelConstants.FILE_NAME, "入库表列表");
		 mv.addObject(NormalExcelConstants.CLASS, StockIn.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("入库表数据", "导出人:"+sysUser.getRealname(), "入库表"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, stockInList);
		 return mv;
		// return super.exportXls(request, stockIn, StockIn.class, "入库表");
	 }

	 /**
	  * 导出
	  * @return
	  */
	 @RequestMapping(value = "/exportStockInDetail")
	 public ModelAndView exportStockInDetail(HttpServletRequest request, StockInDetail stockInDetail) {
		 // Step.1 组装查询条件
		 QueryWrapper<StockInDetail> queryWrapper = QueryGenerator.initQueryWrapper(stockInDetail, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 // Step.2 获取导出数据
		 List<StockInDetail> pageList = stockInDetailService.list(queryWrapper);
		 List<StockInDetail> exportList = null;

		 // 过滤选中数据
		 String selections = request.getParameter("selections");
		 if (oConvertUtils.isNotEmpty(selections)) {
			 List<String> selectionList = Arrays.asList(selections.split(","));
			 exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		 } else {
			 exportList = pageList;
		 }

		 // Step.3 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 //此处设置的filename无效,前端会重更新设置一下
		 mv.addObject(NormalExcelConstants.FILE_NAME, "入库明细表");
		 mv.addObject(NormalExcelConstants.CLASS, StockInDetail.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("入库明细表报表", "导出人:" + sysUser.getRealname(), "入库明细表"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
	 }

	 /**
	  * 纯明细查询 - 分页
	  */
	 @AutoLog(value = "入库明细-分页列表查询")
	 @ApiOperation(value = "入库明细-分页列表查询", notes = "入库明细-分页列表查询")
	 @GetMapping(value = "/listDetailAll")
	 public Result<IPage<StockInDetailVo>> listDetailAll(
			 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			 StockInDetailVo vo) {
//
		 Page<StockInDetailVo> page = new Page<>(pageNo, pageSize);
		 IPage<StockInDetailVo> pageList = stockInDetailService.listDetailAll(page, vo);
		 return Result.OK(pageList);
	 }

	 /**
	  * 纯明细查询 - 导出
	  */
	 @AutoLog(value = "入库明细-导出")
	 @ApiOperation(value = "入库明细-导出", notes = "入库明细-导出")
	 @GetMapping(value = "/exportDetailAll")
	 public ModelAndView exportDetailAll(HttpServletRequest request, StockInDetailVo vo) {

		 QueryWrapper<StockInDetailVo> queryWrapper = QueryGenerator.initQueryWrapper(new StockInDetailVo(), request.getParameterMap());
		 List<StockInDetailVo> list = stockInDetailService.listDetailAll(vo);

		 // 添加汇总行
		 BigDecimal totalAmount = list.stream()
				 .map(StockInDetailVo::getTotalAmount)
				 .filter(Objects::nonNull)
				 .reduce(BigDecimal.ZERO, BigDecimal::add);
		 BigDecimal totalActualQty = list.stream()
				 .map(StockInDetailVo::getActualQty)
				 .filter(Objects::nonNull)
				 .reduce(BigDecimal.ZERO, BigDecimal::add);
		 BigDecimal totalApplyQty = list.stream()
				 .map(StockInDetailVo::getApplyQty)
				 .filter(Objects::nonNull)
				 .reduce(BigDecimal.ZERO, BigDecimal::add);

		 // 添加汇总行
		 StockInDetailVo summary = new StockInDetailVo();
		 summary.setGoodsName("合计");
		 summary.setGoodsCode("");
		 summary.setStockInNo("");
		 summary.setApplyQty(totalApplyQty);
		 summary.setActualQty(totalActualQty);
		 summary.setTotalAmount(totalAmount);
		 list.add(summary);

		 LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 // Step.3 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 //此处设置的filename无效,前端会重更新设置一下
		 mv.addObject(NormalExcelConstants.FILE_NAME, "入库明细表");
		 mv.addObject(NormalExcelConstants.CLASS, StockInDetailVo.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("入库明细表报表", "导出人:" + loginUser.getRealname(), "入库明细表"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, list);
		 return mv;
	 }
}
