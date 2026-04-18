package org.jeecg.modules.scm.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities;
import org.jeecg.modules.common.enums.ApproveStatusEnum;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.enums.StockEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.scm.vo.PriceOfferDetailVo;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.entity.StockInDetail;
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
import org.jeecg.modules.scm.entity.PriceOfferDetail;
import org.jeecg.modules.scm.entity.PriceOffer;
import org.jeecg.modules.scm.vo.PriceOfferPage;
import org.jeecg.modules.scm.service.IPriceOfferService;
import org.jeecg.modules.scm.service.IPriceOfferDetailService;
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
 * @Description: 报价单
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
@Tag(name="报价单")
@RestController
@RequestMapping("/scm/priceOffer")
@Slf4j
public class PriceOfferController {
	@Autowired
	private IPriceOfferService priceOfferService;
	@Autowired
	private IPriceOfferDetailService priceOfferDetailService;
	@Autowired
	private ISerialNoService serialNoService;
	@Autowired
	 private ISysUserService sysUserService;
	/**
	 * 分页列表查询
	 *
	 * @param priceOffer
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "报价单-分页列表查询")
	@Operation(summary="报价单-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PriceOffer>> queryPageList(PriceOffer priceOffer,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<PriceOffer> queryWrapper = QueryGenerator.initQueryWrapper(priceOffer, req.getParameterMap());
		Page<PriceOffer> page = new Page<PriceOffer>(pageNo, pageSize);
		IPage<PriceOffer> pageList = priceOfferService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 @Operation(summary="报价明细分页查询")
	 @GetMapping("/detailPage")
	 public Result<IPage<PriceOfferDetailVo>> detailPage(
			 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			 @RequestParam(required = false) String customerId,
			 @RequestParam(required = false) String salesmanId,
			 @RequestParam(required = false) String productCode,
			 @RequestParam(required = false) String productName,
			 @RequestParam(required = false) String customProductName) {

		 if (StringUtils.isEmpty(customerId)) {
			 return Result.OK(new Page<>());
		 }

		 PriceOfferDetailVo vo = new PriceOfferDetailVo();
		 vo.setCustomerId(customerId);
		 vo.setSalesmanId(salesmanId);
		 vo.setProductCode(productCode);
		 vo.setProductName(productName);
		 vo.setCustomProductName(customProductName);

		 Page<PriceOfferDetailVo> page = new Page<>(pageNo, pageSize);
		 IPage<PriceOfferDetailVo> result = priceOfferService.getDetailVoPage(page, vo);

		 return Result.OK(result);
	 }

	/**
	 *   添加
	 *
	 * @param priceOfferPage
	 * @return
	 */
	@AutoLog(value = "报价单-添加")
	@Operation(summary="报价单-添加")
    @RequiresPermissions("scm:mis_price_offer:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PriceOfferPage priceOfferPage) {
		PriceOffer priceOffer = new PriceOffer();
		BeanUtils.copyProperties(priceOfferPage, priceOffer);
		//生成报价单号
		priceOffer.setOfferNo(serialNoService.generateSerialNo(SerialNoPrefixEnum.QUOTATION_ORDER.getPrefix()));
		SysUser sysUser = sysUserService.getById(priceOffer.getSalesmanId());
		if(sysUser != null){
			priceOffer.setSalesmanName(sysUser.getRealname());
		}
		priceOffer.setDelFlag("0");
		priceOffer.setApproveStatus("0");
		priceOfferService.saveMain(priceOffer, priceOfferPage.getPriceOfferDetailList());
		return Result.OK("添加成功！");
	}
	 /**
	  *  审核
	  *
	  * @param priceOfferPage
	  * @return
	  */
	 @AutoLog(value = "报价单-审核")
	 @Operation(summary="报价单-审核")
	 @RequestMapping(value = "/approve", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> approve(@RequestBody PriceOfferPage priceOfferPage) {
		 PriceOffer priceOffer = new PriceOffer();
		 BeanUtils.copyProperties(priceOfferPage, priceOffer);
		 PriceOffer priceOfferEntity = priceOfferService.getById(priceOffer.getId());
		 if(priceOfferEntity==null) {
			 return Result.error("未找到对应数据");
		 }
		 LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 priceOffer.setApproverId(loginUser.getId());  // 记录实际执行人
		 priceOffer.setApproverName(loginUser.getRealname());
		 priceOffer.setApproveTime(new DateTime());

		 if(priceOffer.getApproveStatus().equals(ApproveStatusEnum.PASS.getCode())) {

			 priceOffer.setStatus("1"); // 启用
			 priceOfferService.updateById(priceOffer);

			 // 【关键】审核通过后，禁用历史重复价格
			 priceOfferService.disableDuplicateAfterApprove(priceOffer.getId());
		 }
		 else if(priceOffer.getApproveStatus().equals(ApproveStatusEnum.REJECT.getCode())) {
			// priceOfferService.updateMain(priceOffer, priceOfferPage.getPriceOfferDetailList());
			 priceOfferService.updateById(priceOffer);
		 }

		return Result.OK("审核成功");
	}

	 /**
	  * 禁用历史重复价格
	  */
	 private void disableDuplicateHistory(String customerId, PriceOfferDetail newDetail) {
		 // 查询该客户该产品的其他有效明细（排除自己）
		 List<PriceOfferDetail> historyList = priceOfferDetailService.lambdaQuery()
				 .ne(PriceOfferDetail::getId, newDetail.getId())           // 排除自己
				 .eq(PriceOfferDetail::getProductId, newDetail.getProductId()) // 同一产品
				 .eq(PriceOfferDetail::getStatus, "1")                     // 有效的
				 .eq(PriceOfferDetail::getDelFlag, "0")
				 .list();

		 // 过滤同一客户（通过主表关联查）
		 for (PriceOfferDetail history : historyList) {
			 // 查历史明细所属报价单的客户
			 PriceOffer historyOffer = priceOfferService.getById(history.getOfferId());

			 // 判断：同一客户 + 维度相同
			 if (customerId.equals(historyOffer.getCustomerId())
					 && isSameDimension(history, newDetail)) {

				 // 禁用历史记录
				 history.setStatus("0");
				 history.setDisabledReason("被报价单[" + newDetail.getOfferId() + "]-"
						 + "明细[" + newDetail.getId() + "]替代");
				 priceOfferDetailService.updateById(history);
			 }
		 }
	 }

	 /**
	  * 判断维度是否完全相同
	  */
	 private boolean isSameDimension(PriceOfferDetail old, PriceOfferDetail neu) {
		 return Objects.equals(old.getPriceType(), neu.getPriceType())
				 && Objects.equals(old.getPackageId(), neu.getPackageId())
				 && isOverlap(old.getQtyMin(), old.getQtyMax(),
				 neu.getQtyMin(), neu.getQtyMax())
				 && Objects.equals(old.getEffectiveDate(), neu.getEffectiveDate())
				 && Objects.equals(old.getExpiryDate(), neu.getExpiryDate());
	 }

	 /**
	  * 判断数量区间是否重叠
	  */
	 private boolean isOverlap(BigDecimal min1, BigDecimal max1,
							   BigDecimal min2, BigDecimal max2) {
		 return min1.compareTo(max2) <= 0 && min2.compareTo(max1) <= 0;
	 }

	/**
	 *  编辑
	 *
	 * @param priceOfferPage
	 * @return
	 */
	@AutoLog(value = "报价单-编辑")
	@Operation(summary="报价单-编辑")
    @RequiresPermissions("scm:mis_price_offer:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PriceOfferPage priceOfferPage) {
		PriceOffer priceOffer = new PriceOffer();
		BeanUtils.copyProperties(priceOfferPage, priceOffer);
		PriceOffer priceOfferEntity = priceOfferService.getById(priceOffer.getId());
		if(priceOfferEntity==null) {
			return Result.error("未找到对应数据");
		}
		priceOfferService.updateMain(priceOffer, priceOfferPage.getPriceOfferDetailList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "报价单-通过id删除")
	@Operation(summary="报价单-通过id删除")
    @RequiresPermissions("scm:mis_price_offer:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		priceOfferService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "报价单-批量删除")
	@Operation(summary="报价单-批量删除")
    @RequiresPermissions("scm:mis_price_offer:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.priceOfferService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "报价单-通过id查询")
	@Operation(summary="报价单-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PriceOffer> queryById(@RequestParam(name="id",required=true) String id) {
		PriceOffer priceOffer = priceOfferService.getById(id);
		if(priceOffer==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(priceOffer);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "报价单明细通过主表ID查询")
	@Operation(summary="报价单明细主表ID查询")
	@GetMapping(value = "/queryPriceOfferDetailByMainId")
	public Result<List<PriceOfferDetail>> queryPriceOfferDetailListByMainId(@RequestParam(name="id",required=true) String id) {
		List<PriceOfferDetail> priceOfferDetailList = priceOfferDetailService.selectByMainId(id);
		return Result.OK(priceOfferDetailList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param priceOffer
    */
    @RequiresPermissions("scm:mis_price_offer:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PriceOffer priceOffer) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<PriceOffer> queryWrapper = QueryGenerator.initQueryWrapper(priceOffer, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<PriceOffer> priceOfferList = priceOfferService.list(queryWrapper);

      // Step.3 组装pageList
      List<PriceOfferPage> pageList = new ArrayList<PriceOfferPage>();
      for (PriceOffer main : priceOfferList) {
          PriceOfferPage vo = new PriceOfferPage();
          BeanUtils.copyProperties(main, vo);
          List<PriceOfferDetail> priceOfferDetailList = priceOfferDetailService.selectByMainId(main.getId());
          vo.setPriceOfferDetailList(priceOfferDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "报价单列表");
      mv.addObject(NormalExcelConstants.CLASS, PriceOfferPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("报价单数据", "导出人:"+sysUser.getRealname(), "报价单"));
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
    @RequiresPermissions("scm:mis_price_offer:importExcel")
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
              List<PriceOfferPage> list = ExcelImportUtil.importExcel(file.getInputStream(), PriceOfferPage.class, params);
              for (PriceOfferPage page : list) {
                  PriceOffer po = new PriceOffer();
                  BeanUtils.copyProperties(page, po);
                  priceOfferService.saveMain(po, page.getPriceOfferDetailList());
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
