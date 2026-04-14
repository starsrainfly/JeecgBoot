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
import org.jeecg.modules.wms.entity.DeliveryDetail;
import org.jeecg.modules.wms.entity.Delivery;
import org.jeecg.modules.wms.vo.DeliveryPage;
import org.jeecg.modules.wms.service.IDeliveryService;
import org.jeecg.modules.wms.service.IDeliveryDetailService;
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
 * @Description: 发货表
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
@Tag(name="发货表")
@RestController
@RequestMapping("/wms/delivery")
@Slf4j
public class DeliveryController {
	@Autowired
	private IDeliveryService deliveryService;
	@Autowired
	private IDeliveryDetailService deliveryDetailService;
	
	/**
	 * 分页列表查询
	 *
	 * @param delivery
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "发货表-分页列表查询")
	@Operation(summary="发货表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Delivery>> queryPageList(Delivery delivery,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("sourceType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("logisticsType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<Delivery> queryWrapper = QueryGenerator.initQueryWrapper(delivery, req.getParameterMap(),customeRuleMap);
		Page<Delivery> page = new Page<Delivery>(pageNo, pageSize);
		IPage<Delivery> pageList = deliveryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param deliveryPage
	 * @return
	 */
	@AutoLog(value = "发货表-添加")
	@Operation(summary="发货表-添加")
    @RequiresPermissions("wms:mis_delivery:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody DeliveryPage deliveryPage) {
		Delivery delivery = new Delivery();
		BeanUtils.copyProperties(deliveryPage, delivery);
		deliveryService.saveMain(delivery, deliveryPage.getDeliveryDetailList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param deliveryPage
	 * @return
	 */
	@AutoLog(value = "发货表-编辑")
	@Operation(summary="发货表-编辑")
    @RequiresPermissions("wms:mis_delivery:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody DeliveryPage deliveryPage) {
		Delivery delivery = new Delivery();
		BeanUtils.copyProperties(deliveryPage, delivery);
		Delivery deliveryEntity = deliveryService.getById(delivery.getId());
		if(deliveryEntity==null) {
			return Result.error("未找到对应数据");
		}
		deliveryService.updateMain(delivery, deliveryPage.getDeliveryDetailList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "发货表-通过id删除")
	@Operation(summary="发货表-通过id删除")
    @RequiresPermissions("wms:mis_delivery:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		deliveryService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "发货表-批量删除")
	@Operation(summary="发货表-批量删除")
    @RequiresPermissions("wms:mis_delivery:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.deliveryService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "发货表-通过id查询")
	@Operation(summary="发货表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Delivery> queryById(@RequestParam(name="id",required=true) String id) {
		Delivery delivery = deliveryService.getById(id);
		if(delivery==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(delivery);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "发货明细通过主表ID查询")
	@Operation(summary="发货明细主表ID查询")
	@GetMapping(value = "/queryDeliveryDetailByMainId")
	public Result<List<DeliveryDetail>> queryDeliveryDetailListByMainId(@RequestParam(name="id",required=true) String id) {
		List<DeliveryDetail> deliveryDetailList = deliveryDetailService.selectByMainId(id);
		return Result.OK(deliveryDetailList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param delivery
    */
    @RequiresPermissions("wms:mis_delivery:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Delivery delivery) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<Delivery> queryWrapper = QueryGenerator.initQueryWrapper(delivery, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<Delivery> deliveryList = deliveryService.list(queryWrapper);

      // Step.3 组装pageList
      List<DeliveryPage> pageList = new ArrayList<DeliveryPage>();
      for (Delivery main : deliveryList) {
          DeliveryPage vo = new DeliveryPage();
          BeanUtils.copyProperties(main, vo);
          List<DeliveryDetail> deliveryDetailList = deliveryDetailService.selectByMainId(main.getId());
          vo.setDeliveryDetailList(deliveryDetailList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "发货表列表");
      mv.addObject(NormalExcelConstants.CLASS, DeliveryPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("发货表数据", "导出人:"+sysUser.getRealname(), "发货表"));
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
    @RequiresPermissions("wms:mis_delivery:importExcel")
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
              List<DeliveryPage> list = ExcelImportUtil.importExcel(file.getInputStream(), DeliveryPage.class, params);
              for (DeliveryPage page : list) {
                  Delivery po = new Delivery();
                  BeanUtils.copyProperties(page, po);
                  deliveryService.saveMain(po, page.getDeliveryDetailList());
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
