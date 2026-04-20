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

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
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
import org.jeecg.modules.scm.entity.CustomerAddress;
import org.jeecg.modules.scm.entity.CustomerQualification;
import org.jeecg.modules.scm.entity.CustomerContact;
import org.jeecg.modules.scm.entity.CustomerSalesman;
import org.jeecg.modules.scm.entity.Customer;
import org.jeecg.modules.scm.vo.CustomerPage;
import org.jeecg.modules.scm.service.ICustomerService;
import org.jeecg.modules.scm.service.ICustomerAddressService;
import org.jeecg.modules.scm.service.ICustomerQualificationService;
import org.jeecg.modules.scm.service.ICustomerContactService;
import org.jeecg.modules.scm.service.ICustomerSalesmanService;
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
 * @Description: 客户信息
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
@Tag(name="客户信息")
@RestController
@RequestMapping("/scm/customer")
@Slf4j
public class CustomerController {
	@Autowired
	private ICustomerService customerService;
	@Autowired
	private ICustomerAddressService customerAddressService;
	@Autowired
	private ICustomerQualificationService customerQualificationService;
	@Autowired
	private ICustomerContactService customerContactService;
	@Autowired
	private ICustomerSalesmanService customerSalesmanService;
	@Autowired
	private ISerialNoService serialNoService;
	@Autowired
	private ISysUserService sysUserService;
	/**
	 * 分页列表查询
	 *
	 * @param customer
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "客户信息-分页列表查询")
	@Operation(summary="客户信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Customer>> queryPageList(Customer customer,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("customerType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<Customer> queryWrapper = QueryGenerator.initQueryWrapper(customer, req.getParameterMap(),customeRuleMap);
		Page<Customer> page = new Page<Customer>(pageNo, pageSize);
		IPage<Customer> pageList = customerService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param customerPage
	 * @return
	 */
	@AutoLog(value = "客户信息-添加")
	@Operation(summary="客户信息-添加")
    @RequiresPermissions("scm:mis_customer:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CustomerPage customerPage) {
		Customer customer = new Customer();
		BeanUtils.copyProperties(customerPage, customer);

		if (CollectionUtil.isEmpty(customerPage.getCustomerAddressList())) {
			return Result.error("客户收货地址不能为空");
		}

		String customerCode = serialNoService.generateCustomerCode(customer.getTradeType(),
				customer.getDistrictCode(),customer.getRegionCode());
		customer.setCustomerCode(customerCode);
		String districtName = customer.convertisDistrictCode().replace("/","");
		customer.setDistrictName(districtName);
		SysUser sysUserById = sysUserService.getById(customer.getSalesmanId());
		String salesmanName = sysUserById.getRealname();
		customer.setSalesmanName(salesmanName);

		customerService.saveMain(customer, customerPage.getCustomerAddressList(),customerPage.getCustomerQualificationList(),customerPage.getCustomerContactList(),customerPage.getCustomerSalesmanList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param customerPage
	 * @return
	 */
	@AutoLog(value = "客户信息-编辑")
	@Operation(summary="客户信息-编辑")
    @RequiresPermissions("scm:mis_customer:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CustomerPage customerPage) {
		Customer customer = new Customer();
		BeanUtils.copyProperties(customerPage, customer);
		Customer customerEntity = customerService.getById(customer.getId());
		if(customerEntity==null) {
			return Result.error("未找到对应数据");
		}

		if (CollectionUtil.isEmpty(customerPage.getCustomerAddressList())) {
			return Result.error("客户收货地址不能为空");
		}

		String districtName = customer.convertisDistrictCode().replace("/","");
		customer.setDistrictName(districtName);
		SysUser sysUserById = sysUserService.getById(customer.getSalesmanId());
		String salesmanName = sysUserById.getRealname();
		customer.setSalesmanName(salesmanName);

		customerService.updateMain(customer, customerPage.getCustomerAddressList(),customerPage.getCustomerQualificationList(),customerPage.getCustomerContactList(),customerPage.getCustomerSalesmanList());
		return Result.OK("编辑成功!");
	}

	 @AutoLog(value = "客户信息-审核")
	 @Operation(summary="客户信息-审核")
	 @RequestMapping(value = "/approve", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> approve(@RequestBody CustomerPage customerPage) {
		 Customer customer = new Customer();
		 BeanUtils.copyProperties(customerPage, customer);
		 Customer customerEntity = customerService.getById(customer.getId());
		 if(customerEntity==null) {
			 return Result.error("未找到对应数据");
		 }
		 LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 customer.setApprovalId(loginUser.getId());  // 记录实际执行人
		 customer.setApprovalUser(loginUser.getRealname());
		 customer.setApprovalDate(new DateTime());
//		 customer.setApprovalRemark(customer.getApprovalRemark());
//		 customer.setApprovalStatus(customer.getApprovalStatus());

		 customerService.updateById(customer);

		 return Result.OK("审核成功");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "客户信息-通过id删除")
	@Operation(summary="客户信息-通过id删除")
    @RequiresPermissions("scm:mis_customer:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		customerService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "客户信息-批量删除")
	@Operation(summary="客户信息-批量删除")
    @RequiresPermissions("scm:mis_customer:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.customerService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "客户信息-通过id查询")
	@Operation(summary="客户信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Customer> queryById(@RequestParam(name="id",required=true) String id) {
		Customer customer = customerService.getById(id);
		if(customer==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(customer);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "客户地址通过主表ID查询")
	@Operation(summary="客户地址主表ID查询")
	@GetMapping(value = "/queryCustomerAddressByMainId")
	public Result<List<CustomerAddress>> queryCustomerAddressListByMainId(@RequestParam(name="id",required=true) String id) {
		List<CustomerAddress> customerAddressList = customerAddressService.selectByMainId(id);
		return Result.OK(customerAddressList);
	}
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "客户质证通过主表ID查询")
	@Operation(summary="客户质证主表ID查询")
	@GetMapping(value = "/queryCustomerQualificationByMainId")
	public Result<List<CustomerQualification>> queryCustomerQualificationListByMainId(@RequestParam(name="id",required=true) String id) {
		List<CustomerQualification> customerQualificationList = customerQualificationService.selectByMainId(id);
		return Result.OK(customerQualificationList);
	}
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "客户联系人通过主表ID查询")
	@Operation(summary="客户联系人主表ID查询")
	@GetMapping(value = "/queryCustomerContactByMainId")
	public Result<List<CustomerContact>> queryCustomerContactListByMainId(@RequestParam(name="id",required=true) String id) {
		List<CustomerContact> customerContactList = customerContactService.selectByMainId(id);
		return Result.OK(customerContactList);
	}
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "客户销售员通过主表ID查询")
	@Operation(summary="客户销售员主表ID查询")
	@GetMapping(value = "/queryCustomerSalesmanByMainId")
	public Result<List<CustomerSalesman>> queryCustomerSalesmanListByMainId(@RequestParam(name="id",required=true) String id) {
		List<CustomerSalesman> customerSalesmanList = customerSalesmanService.selectByMainId(id);
		return Result.OK(customerSalesmanList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param customer
    */
    @RequiresPermissions("scm:mis_customer:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Customer customer) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<Customer> queryWrapper = QueryGenerator.initQueryWrapper(customer, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<Customer> customerList = customerService.list(queryWrapper);

      // Step.3 组装pageList
      List<CustomerPage> pageList = new ArrayList<CustomerPage>();
      for (Customer main : customerList) {
          CustomerPage vo = new CustomerPage();
          BeanUtils.copyProperties(main, vo);
          List<CustomerAddress> customerAddressList = customerAddressService.selectByMainId(main.getId());
          vo.setCustomerAddressList(customerAddressList);
          List<CustomerQualification> customerQualificationList = customerQualificationService.selectByMainId(main.getId());
          vo.setCustomerQualificationList(customerQualificationList);
          List<CustomerContact> customerContactList = customerContactService.selectByMainId(main.getId());
          vo.setCustomerContactList(customerContactList);
          List<CustomerSalesman> customerSalesmanList = customerSalesmanService.selectByMainId(main.getId());
          vo.setCustomerSalesmanList(customerSalesmanList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "客户信息列表");
      mv.addObject(NormalExcelConstants.CLASS, CustomerPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("客户信息数据", "导出人:"+sysUser.getRealname(), "客户信息"));
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
    @RequiresPermissions("scm:mis_customer:importExcel")
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
              List<CustomerPage> list = ExcelImportUtil.importExcel(file.getInputStream(), CustomerPage.class, params);
              for (CustomerPage page : list) {
                  Customer po = new Customer();
                  BeanUtils.copyProperties(page, po);
                  customerService.saveMain(po, page.getCustomerAddressList(),page.getCustomerQualificationList(),page.getCustomerContactList(),page.getCustomerSalesmanList());
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
