package cn.stylefeng.guns.core.util;

public enum AccountUpdateWallet {

     BANZHUAN_WALLET_HC("BANZHUAN_WALLET_HC","搬砖HC")
    , BANZHUAN_WALLET_USDT("BANZHUAN_WALLET_USDT","搬砖USDT")
    , OTC_WALLET_HC("OTC_WALLET_HC","法币HC")
    , OTC_WALLET_USDT("OTC_WALLET_USDT","法币USDT")
    ,PROFIT_WALLET_HC("PROFIT_WALLET_HC","挖矿HC")


    ;


    private  String code;

    private String message;

    AccountUpdateWallet(String code, String message) {
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
