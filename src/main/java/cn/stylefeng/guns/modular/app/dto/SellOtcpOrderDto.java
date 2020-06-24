package cn.stylefeng.guns.modular.app.dto;


import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class SellOtcpOrderDto implements Serializable {

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 流水订单号
     */
    private String serialno;

    /**
     * 承兑商id
     */
    private Long userId;

    /**
     * 出售用户电话
     */
    private String phone;

    /**
     *数量
     */
    private Double number;

    /**
     * 可用数量
     */
    private Double supNumber;
    /**
     * 最小数量
     */
    private Double minNumber;
    /**
     * 最大数量
     */
    private Double maxNumber;
    /**
     * 角色id
     */
    private Integer roleId;
    /**
     * 会员账号id
     */
    private Long sellerId;

    /**
     * 支付方式ids集合
     */
    private Object payMethodIds;

    /**
     * 支付类型集合
     */
    private String payMethodType;


    /**
     * 手续费
     *
     */
    private Double feePrice;

    /**
     *  手续费比例
     */
    private Double feeRatio;

    /**
     * 状态：1表示正在进行中，2,表示已完成，3表示已取消
     */
    private Integer status;

    /**
     * 单价
     */
    private Double price;

    /**
     * 总价
     */
    private Double totalPrice;

    /**
     * 类型：1:会员出售，2表示承兑商出售，3表示商户出售，4表示代理商
     */
    private Integer type ;

    /**
     *  自动出售:0表示否，1表示是
     */
    private Integer autoMerchant;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 币种
     */
    private String symbols;

    /**
     * 近一个月的交易量
     */
    private Integer traderNum;

    /**
     * 近一个月的成功率
     */
    private Double successRatio;

}
