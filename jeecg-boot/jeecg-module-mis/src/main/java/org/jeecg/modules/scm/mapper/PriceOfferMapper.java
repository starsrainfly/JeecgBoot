package org.jeecg.modules.scm.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.scm.entity.PriceOffer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.scm.vo.PriceOfferDetailVo;

/**
 * @Description: 报价单
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
public interface PriceOfferMapper extends BaseMapper<PriceOffer> {
    IPage<PriceOfferDetailVo> selectDetailVoPage(Page<PriceOfferDetailVo> page, @Param("priceOfferDetailVo") PriceOfferDetailVo priceOfferDetailVo);
}
