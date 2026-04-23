package org.jeecg.modules.scm.controller;

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

import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.scm.service.ISalesPaymentPlanService;
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
import org.jeecg.modules.scm.entity.ReceiptOrderDetail;
import org.jeecg.modules.scm.entity.ReceiptOrder;
import org.jeecg.modules.scm.vo.ReceiptOrderPage;
import org.jeecg.modules.scm.service.IReceiptOrderService;
import org.jeecg.modules.scm.service.IReceiptOrderDetailService;
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
 * @Description: 收款单
 * @Author: jeecg-boot
 * @Date:   2026-04-23
 * @Version: V1.0
 */
@Tag(name="收款单")
@RestController
@RequestMapping("/scm/receiptOrder")
@Slf4j
public class ReceiptOrderController {
	@Autowired
	private IReceiptOrderService receiptOrderService;
	@Autowired
	private IReceiptOrderDetailService receiptOrderDetailService;
	@Autowired
	private ISerialNoService serialNoService;
	@Autowired
	private ISalesPaymentPlanService paymentPlanService;
	
	/**
	 * 分页列表查询
	 *
	 * @param receiptOrder
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "收款单-分页列表查询")
	@Operation(summary="收款单-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ReceiptOrder>> queryPageList(ReceiptOrder receiptOrder,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("salesmanId", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<ReceiptOrder> queryWrapper = QueryGenerator.initQueryWrapper(receiptOrder, req.getParameterMap(),customeRuleMap);
		Page<ReceiptOrder> page = new Page<ReceiptOrder>(pageNo, pageSize);
		IPage<ReceiptOrder> pageList = receiptOrderService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param receiptOrderPage
	 * @return
	 */
	@AutoLog(value = "收款单-添加")
	@Operation(summary="收款单-添加")
    @RequiresPermissions("scm:mis_receipt_order:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ReceiptOrderPage receiptOrderPage) {
		ReceiptOrder receiptOrder = new ReceiptOrder();
		BeanUtils.copyProperties(receiptOrderPage, receiptOrder);

		//更新明细表中的对应计划的已收金额
		String receiptNo = serialNoService.generateSerialNo(SerialNoPrefixEnum.RECEIPT_VOUCHER.getPrefix());
		receiptOrder.setReceiptNo(receiptNo);

		receiptOrderService.saveMain(receiptOrder, receiptOrderPage.getReceiptOrderDetailList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param receiptOrderPage
	 * @return
	 */
	@AutoLog(value = "收款单-编辑")
	@Operation(summary="收款单-编辑")
    @RequiresPermissions("scm:mis_receipt_order:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ReceiptOrderPage receiptOrderPage) {
		ReceiptOrder receiptOrder = new ReceiptOrder();
		BeanUtils.copyProperties(receiptOrderPage, receiptOrder);
		ReceiptOrder receiptOrderEntity = receiptOrderService.getById(receiptOrder.getId());
		if(receiptOrderEntity==null) {
			return Result.error("未找到对应数据");
		}
		receiptOrderService.updateMain(receiptOrder, receiptOrderPage.getReceiptOrderDetailList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "收款单-通过id删除")
	@Operation(summary="收款单-通过id删除")
    @RequiresPermissions("scm:mis_receipt_order:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		receiptOrderService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "收款单-批量删除")
	@Operation(summary="收款单-批量删除")
    @RequiresPermissions("scm:mis_receipt_order:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.receiptOrderService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "收款单-通过id查询")
	@Operation(summary="收款单-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ReceiptOrder> queryById(@RequestParam(name="id",required=true) String id) {
		ReceiptOrder receiptOrder = receiptOrderService.getById(id);
		if(receiptOrder==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(receiptOrder);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "收款明细通过主表ID查询")
	@Operation(summary="收款明细主表ID查询")
	@GetMapping(value = "/queryReceiptOrderDetailByMainId")
	public Result<List<ReceiptOrderDetail>> queryReceiptOrderDetailListByMainId(@RequestParam(name="id",required=true) String id) {
		List<ReceiptOrderDetail> receiptOrderDetailList = receiptOrderDetailService.selectByMainId(id);
		return Result.OK(receiptOrderDetailList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param receiptOrder
    */
    @RequiresPermissions("scm:mis_receipt_order:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ReceiptOrder receiptOrder) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<ReceiptOrder> queryWrapper = QueryGenerator.initQueryWrapper(receiptOrder, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<ReceiptOrder> receiptOrderList = receiptOrderService.list(queryWrapper);

      // Step.3 组装pageList
      List<ReceiptOrderPage> pageList = new ArrayList<ReceiptOrderPage>();
      for (ReceiptOrder main : receiptOrderList) {
          ReceiptOrderPage vo = new ReceiptOrderPage();
          BeanUtils.copyProperties(main, vo);
          List<ReceiptOrderDetail> receiptOrderDetailList = receiptOrderDetailService.selectByMainId(main.getId());
          vo.setReceiptOrderDetailList(receiptOrderDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "收款单列表");
      mv.addObject(NormalExcelConstants.CLASS, ReceiptOrderPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("收款单数据", "导出人:"+sysUser.getRealname(), "收款单"));
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
    @RequiresPermissions("scm:mis_receipt_order:importExcel")
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
              List<ReceiptOrderPage> list = ExcelImportUtil.importExcel(file.getInputStream(), ReceiptOrderPage.class, params);
              for (ReceiptOrderPage page : list) {
                  ReceiptOrder po = new ReceiptOrder();
                  BeanUtils.copyProperties(page, po);
                  receiptOrderService.saveMain(po, page.getReceiptOrderDetailList());
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
