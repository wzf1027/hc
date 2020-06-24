package cn.stylefeng.guns.modular.system.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.entity.PromotionRate;
import cn.stylefeng.guns.modular.system.service.PromotionRateService;
import cn.stylefeng.guns.modular.system.warpper.PromotionRateWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


/**
 * 直推控制器
 */
@Controller
@RequestMapping("/promotionRateMgr")
public class PromotionRateController extends BaseController {

    private static String PREFIX = "/modular/system/promotion_rate/";

    @Autowired
    private PromotionRateService promotionRateService;

    /**
     * 跳转到管理的首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "promotion_rate.html";
    }



    /**
     * 跳转到添加
     */
    @RequestMapping("/promotion_rate_add")
    public String promotionRateAdd() {
        return PREFIX + "promotion_rate_add.html";
    }


    @RequestMapping("/add")
    @ResponseBody
    public ResponseData add(PromotionRate setting) {
        if(setting.getBonusRatio() == null || setting.getBonusRatio() <0) {
            return ResponseData.error("返利比例不能小于0");
        }
//        if (setting.getNumber() == null || setting.getNumber() <=0){
//            return ResponseData.error("数量不能小于等于0");
//        }
        PromotionRate rate = new PromotionRate();
        rate.setType(setting.getType());
        int count = this.promotionRateService.count(new QueryWrapper<>(rate));
        setting.setLevel(1);
        if(count >=1) {
            PromotionRate old =  this.promotionRateService.getLastPromotionRateOne(rate);
            if(old != null) {
                setting.setLevel(old.getLevel()+1);
            }
        }
        this.promotionRateService.save(setting);
        return SUCCESS_TIP;
    }

    /**
     * 跳转到修改
     */
    @RequestMapping("/promotion_rate_edit")
    public String promotionRateEdit(Long rateId, Model model) {
        PromotionRate rate = this.promotionRateService.getById(rateId);
        model.addAllAttributes(BeanUtil.beanToMap(rate));
        LogObjectHolder.me().set(rate);
        return PREFIX + "promotion_rate_edit.html";
    }


    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(PromotionRate setting) {
        if (ToolUtil.isOneEmpty(setting, setting.getRateId())) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }

        if(setting.getBonusRatio() == null || setting.getBonusRatio() <=0) {
            return ResponseData.error("返利比例不能小于0");
        }
//        if (setting.getNumber() == null || setting.getNumber() <=0){
//            return ResponseData.error("数量不能小于等于0");
//        }
        PromotionRate old = this.promotionRateService.getById(setting.getRateId());
        old.setBonusRatio(setting.getBonusRatio());
      //  old.setNumber(setting.getNumber());
        this.promotionRateService.updateById(old);
        return SUCCESS_TIP;
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam Long rateId) {
        PromotionRate old = this.promotionRateService.getById(rateId);
        PromotionRate rate = new PromotionRate();
        rate.setType(old.getType());
        PromotionRate supOld = this.promotionRateService.getLastPromotionRateOne(rate);
        if(supOld != null) {
            if(old.getLevel() <supOld.getLevel()) {
                return ResponseData.error("请先删除最高等级");
            }
        }
        this.promotionRateService.removeById(rateId);
        return SUCCESS_TIP;
    }


    /**
     * 查询列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public Object list(@RequestParam(required = false) String condition) {

        //根据条件查询
        Page<Map<String, Object>> result = this.promotionRateService.list(condition);
        Page wrapped = new PromotionRateWrapper(result).wrap();
        return LayuiPageFactory.createPageInfo(wrapped);
    }





}
