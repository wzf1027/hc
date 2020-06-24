package cn.stylefeng.guns.core.util;

public enum AccountUpdateType {

    EXCHANGE_USDT("EXCHANGE_USDT","兑换USDT")
    ,EXCHANGE_HC("EXCHANGE_HC","兑换HC")
    ,WITHDRAW_USDT_FROZENBALANCE("WITHDRAW_USDT_FROZENBALANCE","搬砖账户提币可用转入冻结")
    ,WITHDRAW_USDT("WITHDRAW_USDT","搬砖账户提币扣除冻结")
    ,WITHDRAW_USDT_AVAILABLEBALANCE("WITHDRAW_USDT_AVAILABLEBALANCE","搬砖账户提币冻结转入可用")

     ,TR_HC_SLABS_TO_OTC("TR_HC_SLABS_TO_OTC","搬砖HC划转到法币HC")
    ,TR_USDT_SLABS_TO_OTC("TR_USDT_SLABS_TO_OTC","搬砖USDT划转到法币USDT")

    ,TR_USDT_OTC_TO_SLABS("TR_USDT_OTC_TO_SLABS","法币USDT划转到搬砖USDT")
    ,TR_HC_OTC_TO_SLABS("TR_HC_OTC_TO_SLABS","法币HC划转到搬砖HC")

    ,TR_HC_PAID_TO_SLABS("TR_HC_PAID_TO_SLABS","代付HC转到搬砖HC")
    ,TR_HC_PAID_TO_OTC("TR_HC_PAID_TO_OTC","代付HC转到法币HC")

    ,TR_HC_PROFIT_TO_SLABS("TR_HC_PROFIT_TO_SLABS","挖矿HC转到搬砖HC")
    ,TR_HC_PROFIT_TO_OTC("TR_HC_PROFIT_TO_OTC","挖矿HC转到法币HC")

    ,BUY_OTC_USDT("BUY_OTC_USDT","购买USDT")
    ,BUY_OTC_HC("BUY_OTC_HC","购买HC")
    ,SELL_OTC_HC("SELL_OTC_HC","出售HC")
    ,SELL_OTC_USDT("SELL_OTC_USDT","出售USDT")
    ,ACCEPTER_PROFIT("ACCEPTER_PROFIT","承兑挖矿")
    ,SUPERIOR_PROFIT("SUPERIOR_PROFIT","上级承兑挖矿")
    ,REVOCATION_USDT("REVOCATION_USDT","撤销出售USDT")
    ,REVOCATION_HC("REVOCATION_HC","撤销出售HC")

    ,CHANNEL_OTC_HC("CHANNEL_OTC_HC","出售HC取消交易")
    ,CHANNEL_OTC_USDT("CHANNEL_OTC_USDT","出售USDT取消交易")

    ,OPEN_SELL_HC("OPEN_SELL_HC","挂单出售")
    ,CLOSE_SELL_HC("CLOSE_SELL_HC","接单交易关闭")
    ,SUCCESS_SELL_HC("CLOSE_SELL_HC","接单交易成功")

    ,BUY_HC_RETURN_AWARD("BUY_HC_RETURN_AWARD","接单返利")
    ,RCCOMEND_AWARD("RCCOMEND_AWARD","推荐挖矿")
    ;


    private  String code;

    private String message;

    AccountUpdateType(String code, String message) {
        this.code = code;
        this.message = message;
    }


    public String code(){
        return this.code;

    }

    public String message(){
        return  this.message;
    }
}
