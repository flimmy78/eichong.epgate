package com.ec.epcore.service;

import com.ec.config.Global;
import com.ec.constants.EpConstants;
import com.ec.constants.ErrorCodeConstants;
import com.ec.constants.UserConstants;
import com.ec.epcore.cache.*;
import com.ec.epcore.net.client.EpCommClient;
import com.ec.epcore.net.codec.EpEncoder;
import com.ec.epcore.net.proto.CardAuthResp;
import com.ec.epcore.sender.EpMessageSender;
import com.ec.logs.LogConstants;
import com.ec.net.proto.Iec104Constant;
import com.ec.utils.DateUtil;
import com.ec.utils.LogUtil;
import com.ormcore.dao.DB;
import com.ormcore.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(LogUtil.getLogName(UserService.class.getName()));

    public static Map<String, UserCache> epUserMap = new ConcurrentHashMap<String, UserCache>();
    public static Map<Integer, UserCache> epUserIdMap = new ConcurrentHashMap<Integer, UserCache>();

    //orgNo & user_id
    public static Map<Integer, Integer> epOrgNoMap = new ConcurrentHashMap<Integer, Integer>();


    public static void putUserCache(UserCache userCache) {
        if (userCache != null) {
            epUserMap.put(userCache.getAccount(), userCache);
            epUserIdMap.put(userCache.getId(), userCache);
        }

    }

    public static UserCache getUserIdByOrgNo(int orgNo) {
        UserCache userInfo = null;
        List<TblUserInfo> userInfoList = DB.userInfoDao.findUserInfoByOrg(orgNo);
        if (null == userInfoList || userInfoList.size()!=1) {
            logger.error(LogUtil.addExtLog("error!,orgNo"), orgNo);
            return null;
        }
        UserRealInfo u = convertUsrRealInfo(userInfoList.get(0));
        userInfo = convertToCache(u);

        return userInfo;
    }

    public static UserCache convertToCache(UserRealInfo userRealInfo) {
        if (userRealInfo == null)
            return null;

        String usrAccount = userRealInfo.getAccount();
        int usrId = userRealInfo.getId();

        UserCache u = new UserCache(usrId, usrAccount, userRealInfo.getName(), userRealInfo.getAccountId(), userRealInfo.getLevelId(), userRealInfo.getInvitePhone());
        u.setCpyId(userRealInfo.getCpyId());
        u.setCpyNumber(userRealInfo.getCpyNumber());
        u.setMoney(userRealInfo.getMoney());
        u.setPresent(userRealInfo.getPresent());

        u.setRemainAmtWarnPhone("");
        u.setRemainAmtWarnCPhone("");
        u.setRemainAmtWarnValue(999999);
        if (usrAccount != null && usrAccount.length() == 12) {
            TblUserThreshod usrThreshod = DB.userThreshodDao.findUserThreshodInfo(usrId);
            if (null != usrThreshod) {
                u.setRemainAmtWarnPhone(usrThreshod.getPhone());
                u.setRemainAmtWarnCPhone(usrThreshod.getCustomerPhone());
                u.setRemainAmtWarnValue(usrThreshod.getWarnMoney());
            }
        }

        if (epUserMap.get(usrAccount) == null) {
            epUserMap.put(usrAccount, u);
        }
        if (epUserIdMap.get(usrId) == null) {
            epUserIdMap.put(usrId, u);
        }

        return u;
    }

    public static UserCache getUserCache(String account) {
        UserCache userInfo = epUserMap.get(account);
        if (userInfo != null) {
            return userInfo;
        }
        UserRealInfo userRealInfo = findUserRealInfo(account);
        if (userRealInfo == null)
            return null;

        userInfo = convertToCache(userRealInfo);

        //查询并设置用户是否新手
        setUserNewcoupon(userInfo);

        return userInfo;
    }

    public static UserCache getUserCache(int accountId) {
        /*UserCache userInfo = epUserIdMap.get(accountId);
        if (userInfo != null) {
            return userInfo;
        }*/
        UserRealInfo userRealInfo = findUserRealInfo(accountId);
        if (userRealInfo == null)
            return null;

        UserCache userInfo = convertToCache(userRealInfo);
        //查询并设置用户是否新手
        setUserNewcoupon(userInfo);

        return userInfo;
    }


    public static ChargeCardCache convertCardToCache(TblChargeCard tabCardInfo) {
        if (tabCardInfo == null)
            return null;

        boolean allowMutliCharge = false;
        int cardType = tabCardInfo.getUc_pay_mode();
        if (cardType == EpConstants.CARD_THIRD_NORMAL ||
                cardType == EpConstants.CARD_THIRD_PRIVATE ||
                cardType == EpConstants.CARD_CO_CREDIT) {
            allowMutliCharge = true;
        }
        ChargeCardCache card = new ChargeCardCache(
                tabCardInfo.getPk_UserCard(), tabCardInfo.getUc_InternalCardNumber(),
                tabCardInfo.getUc_ExternalCardNumber(), tabCardInfo.getUc_CompanyNumber(),
                tabCardInfo.getUc_UserId(), tabCardInfo.getUc_Status(), tabCardInfo.getIs_valid(), tabCardInfo.getUc_pay_mode(),
                allowMutliCharge,tabCardInfo.getAccount_id()
        );


        return card;
    }


    public static void addAmt(int accountId, BigDecimal amt, BigDecimal present, String serialNo) {
        try {
            logger.info("addAmt start,accountId:{},amt:{},serialNo:{}", new Object[]{accountId, amt, serialNo});

            UserCache uc = getUserCache(accountId);
            if (uc == null) {
                logger.info("addAmt fail,accountId:{},amt:{},getUserCache()==null", accountId, amt);
                return;
            }

            TblUserInfo u = new TblUserInfo();
            u.setId(accountId);
            u.setAccountId(uc.getAccountId());
            u.setBalance(amt);
            u.setPresent(present);

            DB.userInfoDao.addCost(u);

            logger.info("addAmt finish!accountId:{},amt:{},serialNo:{}", new Object[]{accountId, amt, serialNo});
        } catch (Exception e) {
            logger.error("addAmt exception,getStackTrace:{}", e.getStackTrace());
        }
    }

    public static void subAmt(int accountId, BigDecimal amt, BigDecimal present, String serialNo) {
        try {
            logger.info("subAmt start. accountId:{},amt:{},present:{},serialNo:{}", new Object[]{accountId, amt, present, serialNo});
            UserCache uc = getUserCache(accountId);
            if (uc == null) {
                logger.info("subAmt fail,accountId:{},amt:{},getUserCache()==null", accountId, amt);
                return;
            }

            TblUserInfo u = new TblUserInfo();
            u.setId(uc.getId());
            u.setAccountId(uc.getAccountId());
            u.setBalance(amt);
            u.setPresent(present);

            DB.userInfoDao.subCost(u);

            logger.info("subAmt finish!accountId:{},amt:{},present:{},serialNo:{}", new Object[]{accountId, amt, present, serialNo});


        } catch (Exception e) {
            logger.error("subAmt exception,getStackTrace:{}", e.getStackTrace());
        }


    }

    public static CardAuthResp checkUserCard(String epCode, ChargeCardCache cardCache, String userPasswordMd5) {
        CardAuthResp ret = new CardAuthResp();
        //查电桩
        ElectricPileCache epCache = EpService.getEpByCode(epCode);
        EpService.getEpRealStatus(epCache);

        if (epCache == null) {
            ret.setErrorCode(EpConstants.E_CARD_NOT_FIND_EP);
            return ret;
        }
        if (cardCache == null) {
            ret.setErrorCode(EpConstants.E_CARD_INVALID);
            return ret;
        }
        if (cardCache.getStatus() != 0) {
            ret.setErrorCode(EpConstants.E_CARD_LOSS);
            return ret;
        }
        int userId = cardCache.getUserId();
        UserRealInfo u = findCardUserInfo(userId);

        if (u == null) {
            ret.setErrorCode(EpConstants.E_CARD_NOT_BIND_USER);
            return ret;
        }
        if (u.getStatus() != 1)//不是正常用户
        {
            ret.setErrorCode(EpConstants.E_CARD_NO_RIGHT);
            return ret;
        }
        int errorCode = epCache.canAuth(false);
        if (errorCode > 0) {
            ret.setErrorCode(errorCode);
            return ret;
        }

        if (u.getPassword().compareTo(userPasswordMd5) != 0) {
            ret.setErrorCode(EpConstants.E_CARD_AUTH_ERROR_PW);
            return ret;
        }

        int cardType = cardCache.getCardType();
        if (cardType != EpConstants.CARD_NORMAL &&
                cardType != EpConstants.CARD_NORMAL_PRIVATE &&
                cardType != EpConstants.CARD_CO_CREDIT &&
                cardType != EpConstants.CARD_THIRD_NORMAL &&
                cardType != EpConstants.CARD_THIRD_PRIVATE) {
            ret.setErrorCode(EpConstants.E_CARD_INVALID);
            return ret;
        }

		
		/*if(cardType==EpConstants.CARD_NORMAL && epCache.getCompany_number()>0)
        {
			//普通卡不能刷带公司标识的桩
			ret.setErrorCode(EpConstants.E_CARD_NO_RIGHT);
			return ret;
		}
		
		
		//范围卡
		if( cardType==EpConstants.CARD_NORMAL_PRIVATE||
			cardType==EpConstants.CARD_THIRD_PRIVATE||
			cardType==EpConstants.CARD_CO_CREDIT)
		{
			int coNoOfCard = cardCache.getCompanyNumber();
			int coNoOfEp = epCache.getCompany_number();
			if(coNoOfCard<=0 ||coNoOfEp != coNoOfCard)
			{
				logger.debug("checkUserCard,card pay_mod:{},card'companyNumber card:{},ep''companyNumber",
						new Object[]{cardType,coNoOfCard,coNoOfEp});
				ret.setErrorCode(EpConstants.E_CARD_NO_RIGHT);
				return ret;
			}
			
		}*/
        if (!epCache.canOrgOperate(cardCache.getCompanyNumber())) {
            logger.error(LogUtil.addBaseExtLog("cardType|cardInNo"),
                    new Object[]{LogConstants.FUNC_CARD_AUTH, epCode, 0, cardCache.getCompanyNumber(), cardCache.getUserId(), cardType, cardCache.getCardInNo()});
            ret.setErrorCode(EpConstants.E_CARD_NO_RIGHT);
            return ret;
        }
        int cardStatus = 0;
        if (ret.getErrorCode() == 0 && cardCache.getValid() == 1) {
            cardStatus = EpService.getValidFromDB(cardCache.getCompanyNumber());
        }
        ret.setCardStatus(cardStatus);
        /*if (cardType == EpConstants.CARD_CO_CREDIT) {
            ret.setIsFrozenAmt(2);
        } else {*/
            ret.setIsFrozenAmt(1);

        //}
        int remainAmt = u.getMoney().multiply(Global.DecTime2).intValue();
        ret.setRemainAmt(remainAmt);

        return ret;
    }

    public static void handleSelfCard(EpCommClient epCommClient, String epCode, int epGunNo, String inCardNo, String userPasswordMd5, byte[] cmdTimes) {
        ChargeCardCache cardCache = getCard(inCardNo);

        String outCardNo = "";
        int remainAmt = 0;
        int success = 0;
        int errorCode = 0;
        int payMode = 1;
        int isFrozenAmt = 1;

        CardAuthResp ret = checkUserCard(epCode, cardCache, userPasswordMd5);
        if (ret != null) {
            success = (ret.getErrorCode() != 0) ? 0 : 1;
            if (success == 1) {
                logger.info("card charge userCardAuth success,epCode:{},epGunNo:{},inCardNo:{}",
                        new Object[]{epCode, epGunNo, inCardNo});
            } else {
                logger.info("card charge userCardAuth fail,epCode:{},epGunNo:{},inCardNo:{},errorCode:{}",
                        new Object[]{epCode, epGunNo, inCardNo, ret.getErrorCode()});

            }

            UserCache userCache = null;
            if (cardCache != null)
                userCache = UserService.getUserCache(cardCache.getUserId());

            if (userCache != null && cardCache != null) {
                EpGunCache epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
                AuthUserCache authUser = new AuthUserCache(userCache.getId(), userCache.getAccount(), DateUtil.getCurrentSeconds(), (short) 3);
                epGunCache.setAuthCache(authUser);
                //payMode = EpChargeService.getCardPayMode(cardCache.getCardType());

            }
            remainAmt = ret.getRemainAmt();
            errorCode = ret.getErrorCode();
            isFrozenAmt = ret.getIsFrozenAmt();
            if (cardCache != null) {
                outCardNo = cardCache.getCardOutNo();
            }
        } else {
            logger.error("checkUserCard exception!", new Object[]{epCode, inCardNo, userPasswordMd5});

        }

        byte[] data = EpEncoder.do_card_auth_resq(epCode, epGunNo,
                inCardNo, outCardNo, payMode, isFrozenAmt,
                remainAmt, success, errorCode);

        EpMessageSender.sendMessage(epCommClient, 0, 0, Iec104Constant.C_CARD_AUTH_RESP, data, cmdTimes, epCommClient.getVersion());

    }

    public static void handleVinCard(EpCommClient epCommClient, String epCode, int epGunNo, String inCardNo, String userPasswordMd5, byte[] cmdTimes) {
        ChargeCardCache cardCache = getCard(inCardNo);

        String outCardNo = "";
        int remainAmt = 0;
        int success = 0;
        int errorCode = 0;
        int payMode = 1;
        int isFrozenAmt = 1;
        List<TblCarVin> carVinList = null;

        CardAuthResp ret = checkUserCard(epCode, cardCache, userPasswordMd5);
        if (ret != null) {
            if (ret.getCardStatus() == 1) {
                success = 2;
                carVinList= DB.carVinDao.selectByUserId(cardCache.getUserId());
                if(carVinList==null || carVinList.size()==0)
                {
                    logger.error("not find carVin rela in db,userId:{}",cardCache.getUserId());
                    ret.setErrorCode(10);
                } else if (carVinList.size()>10) {
                    logger.error("find carVin rela in db > 10,userId:{}",cardCache.getUserId());
                    ret.setErrorCode(11);
                } else {
                    logger.debug(LogUtil.addBaseExtLog("carVinList"),
                            new Object[]{LogConstants.FUNC_CARD_AUTH, epCode, 0, cardCache.getCompanyNumber(), cardCache.getUserId(), carVinList});
                }
            }
            else
                success = (ret.getErrorCode() != 0) ? 0 : 1;
            if (success == 1) {
                logger.info("card charge userCardAuth success,epCode:{},epGunNo:{},inCardNo:{}",
                        new Object[]{epCode, epGunNo, inCardNo});
            } else {
                logger.info("card charge userCardAuth fail,epCode:{},epGunNo:{},inCardNo:{},errorCode:{}",
                        new Object[]{epCode, epGunNo, inCardNo, ret.getErrorCode()});

            }

            UserCache userCache = null;
            if (cardCache != null)
                userCache = UserService.getUserCache(cardCache.getUserId());

            if (userCache != null && cardCache != null) {
                EpGunCache epGunCache = EpGunService.getEpGunCache(epCode, epGunNo);
                AuthUserCache authUser = new AuthUserCache(userCache.getId(), userCache.getAccount(), DateUtil.getCurrentSeconds(), (short) 3);
                epGunCache.setAuthCache(authUser);
                //payMode = EpChargeService.getCardPayMode(cardCache.getCardType());

            }
            remainAmt = ret.getRemainAmt();
            errorCode = ret.getErrorCode();
            isFrozenAmt = ret.getIsFrozenAmt();
            if (cardCache != null) {
                outCardNo = cardCache.getCardOutNo();
            }
        } else {
            logger.error("checkUserCard exception!", new Object[]{epCode, inCardNo, userPasswordMd5});

        }

        byte[] data = EpEncoder.do_vin_auth_resq(epCode, epGunNo,
                inCardNo, outCardNo, payMode, isFrozenAmt,
                remainAmt, success, errorCode, carVinList);

        EpMessageSender.sendMessage(epCommClient, 0, 0, Iec104Constant.C_VIN_AUTH_RESP, data, cmdTimes, epCommClient.getVersion());
    }

    public static int insertBigCard(String innerCardNo, int orgNo, int userId) {
        TblChargeCard card = new TblChargeCard();

        card.setUc_Balance(new BigDecimal(0.0));
        card.setUc_CompanyNumber(orgNo);
        card.setUc_UserId(userId);
        card.setUc_Status(0);
        card.setUc_InternalCardNumber(innerCardNo);
        card.setUc_ExternalCardNumber(innerCardNo);
        card.setUc_pay_mode(12);

        return DB.chargeCardDao.insertCard(card);
    }

    public static ChargeCardCache getCard(String innerCardNo) {

        String uc_InternalCardNumber = innerCardNo;
        List<TblChargeCard> cardList = DB.chargeCardDao.findCard(uc_InternalCardNumber);
        if (cardList == null || cardList.size() < 1) {
            logger.error(LogUtil.addExtLog("inCardNo|cardList"), uc_InternalCardNumber, cardList);
            return null;
        }

        TblChargeCard cardInDb = cardList.get(0);
        ChargeCardCache cardCache = UserService.convertCardToCache(cardInDb);

        int userId = cardInDb.getUc_UserId();
        UserCache userCache = UserService.getUserCache(userId);

        if (userCache == null) {
            logger.error(LogUtil.addExtLog("inCardNo| not find card user"), innerCardNo, userId);
            return null;
        }
        return cardCache;
    }

    public static void handleUserCardAuth(EpCommClient epCommClient, String epCode, int epGunNo, String inCardNo, String userPasswordMd5, int cardOrigin
            , byte[] cmdTimes) {
        switch (cardOrigin) {
            case 0:

                handleSelfCard(epCommClient, epCode, epGunNo, inCardNo, userPasswordMd5, cmdTimes);
                break;
            case 1:
                handleVinCard(epCommClient, epCode, epGunNo, inCardNo, userPasswordMd5, cmdTimes);
                break;
            case 5:
                //EpGunService.handleCardAuth(epCode,epGunNo,cardOrigin,inCardNo,"wm1234567",cmdTimes);
                break;
            default:
                logger.error("card charge userCardAuth fail,epCode:{},epGunNo:{},inCardNo:{},cardOrigin:{}",
                        new Object[]{epCode, epGunNo, inCardNo, cardOrigin});
                break;
        }

    }

    public static BigDecimal getRemainBalance(int accountId) {
        UserRealInfo u = findUserRealInfo(accountId);
        //假设数据
        if (null != u) {
            return u.getMoney();
        }
        return new BigDecimal(0.0);
    }

    /**
     * @param Account
     * @param passwordMd5
     * @return 0:成功,1:没找到用户;2:账户被冻结;3:密码错误;4:桩没绑定费率;5:有未支付的订单;6:余额不够
     */
    public static int checkUser(String epCode, String account, byte[] passwordMd5) {

        int ret = 0;
        UserRealInfo userRealInfo = findUserRealInfo(account);
        if (null == userRealInfo)
            return 1;
        if (1 != userRealInfo.getStatus()) {
            return 2;
        }

        String sPw = new String(passwordMd5);
        if (sPw.compareTo(userRealInfo.getPassword()) != 0) {
            return 3;
        }

        RateInfoCache rateInfo = RateService.getRateInfo(epCode);

        if (rateInfo == null) {
            return 4;
        }
        int userId = userRealInfo.getId();
        int unpayNum = DB.chargeOrderDao.getUnpayOrder(userId);
        if (unpayNum > 0) {
            return 5;
        }
        BigDecimal minAmt = new BigDecimal(1.0);
        if (userRealInfo.getMoney().compareTo(rateInfo.getRateInfo().getMinFreezingMoney()) < 0 || userRealInfo.getMoney().compareTo(minAmt) < 0) {
            return 6;
        }
        int num = DB.chargeOrderDao.getUnpayOrder(userId);
        if (num > 0) {
            return 8;
        }

        return 0;

    }

    public static int insertPartnerUser(String account, int partnerId) {
		/*int norm_origin = 0;
		
		Map<String,Object> userMap =  new HashMap<String,Object> ();
		
		userMap.put("user_account",account);
		//userMap.put("norm_origin",partnerId);
		userMap.put("user_password",WmIce104Util.MD5Encode("123456".getBytes()));
		
		userMap.put("user_leval",6);
		userMap.put("user_status",1);
		
		int user_id = DB.userInfoDao.insertUser(userMap);
	    
		Map<String,Object> normalUser =  new HashMap<String,Object> ();
		normalUser.put("user_id",user_id);
		normalUser.put("norm_origin",norm_origin);
		DB.userInfoDao.insertNormalUser(normalUser);*/

        return 0;
    }

    public static ChargeCardCache getChargeCardCache(int pkUserCard) {
        ChargeCardCache cardCache = null;
        List<TblChargeCard> chargeCardList = DB.chargeCardDao.findCardById(pkUserCard);
        if (chargeCardList != null && chargeCardList.size() >= 1) {
            TblChargeCard cardInDb = chargeCardList.get(0);
            cardCache = UserService.convertCardToCache(cardInDb);
        }
        return cardCache;
    }

    public static UserRealInfo convertUsrRealInfo(TblUserInfo tblUserInfo) {
        try {
            if (tblUserInfo == null)
                return null;

            UserRealInfo u = new UserRealInfo();

            int usrId = tblUserInfo.getId();
            String usrAccount = tblUserInfo.getPhone();

            u.setName(tblUserInfo.getName());
            u.setAccount(usrAccount);
            u.setId(usrId);
            u.setPassword(tblUserInfo.getPassword());
            u.setStatus(tblUserInfo.getStatus());
            u.setLevel(tblUserInfo.getLevel());

            u.setDeviceid(tblUserInfo.getDeviceid());
            u.setInvitePhone(tblUserInfo.getInvitePhone());
            u.setMoney(tblUserInfo.getBalance());
            u.setPresent(tblUserInfo.getPresent());
            u.setAccountId(tblUserInfo.getAccountId());
            u.setCpyId(tblUserInfo.getCpyId());
            u.setCpyNumber(tblUserInfo.getCpyNumber());
            u.setLevelId(tblUserInfo.getLevelId());
            u.setCpyNumber(tblUserInfo.getCpyNumber());

            logger.info("convertUsrRealInfo,u.getId():{},u.getLevel():{}", u.getId(), u.getLevel());


            return u;
        } catch (Exception e) {
            logger.error("convertUsrRealInfo, e.getMessage():{}", e.getMessage());
            return null;
        }
    }

    public static UserRealInfo findUserRealInfo(int userId) {
        UserRealInfo userInfo = null;

        List<TblUserInfo> userInfoList = DB.userInfoDao.findOrgUserById(userId);
        if (null == userInfoList || userInfoList.size() != 1) {
            userInfoList = DB.userInfoDao.findUserInfoById(userId);
        }
        if (null != userInfoList && userInfoList.size() < 1) {
            return userInfo;
        }

        TblUserInfo dbUser = userInfoList.get(0);

        return convertUsrRealInfo(dbUser);
    }

    public static UserRealInfo findCardUserInfo(int userId) {
        UserRealInfo userInfo = null;

        List<TblUserInfo> userInfoList = DB.userInfoDao.findCardUserById(userId);
        if (null != userInfoList && userInfoList.size() < 1) {
            return userInfo;
        }

        TblUserInfo dbUser = userInfoList.get(0);

        return convertUsrRealInfo(dbUser);
    }

    public static UserRealInfo findUserRealInfo(String userAccount) {
        UserRealInfo userInfo = null;

        List<TblUserInfo> userInfoList = DB.userInfoDao.findUserInfoByPhone(userAccount);
        if (null != userInfoList && userInfoList.size() < 1) {
            return userInfo;
        }

        TblUserInfo dbUser = userInfoList.get(0);

        return convertUsrRealInfo(dbUser);
    }

    /**
     * 设置内存中用户新手券状态
     *
     * @param userCache
     */

    public static void setUserNewcoupon(UserCache userCache) {
        if (userCache == null)
            return;
        //判断该用户是否读过数据库，初始化userCache.getNewcouponAcStatus()=-1，userCache.getNewcouponDcStatus()=-1

        if (userCache.getNewcouponAcStatus() >= 0 && userCache.getNewcouponDcStatus() >= 0)
            return;
        List<TblUserNewcoupon> userNewcouponList = DB.userNewcouponDao.select(userCache.getId());
        if (userNewcouponList == null || userNewcouponList.size() <= 0) {
            getChargedInfoByUser(userCache);
            return;
        }
        TblUserNewcoupon newcoupon = userNewcouponList.get(0);
        userCache.setNewcouponAcStatus(newcoupon.getAcStatus());
        userCache.setNewcouponDcStatus(newcoupon.getDcStatus());

    }

    /**
     * 从充电订单中获取用户是否已经充过电
     *
     * @param userCache
     */
    public static void getChargedInfoByUser(UserCache userCache) {
        if (userCache == null)
            return;

        userCache.setNewcouponAcStatus(0);
        userCache.setNewcouponDcStatus(0);
        List<TblElectricPile> epList = DB.epClientDao.getEpTypeByUserChargeOrder(userCache.getId());
        if (epList != null && epList.size() >= 1) {
            for (int i = 0; i < epList.size(); i++) {
                TblElectricPile ep = epList.get(i);
                if (ep.getCurrentType() == EpConstants.EP_AC_TYPE)
                    userCache.setNewcouponAcStatus(1);
                else if (ep.getCurrentType() == EpConstants.EP_DC_TYPE)
                    userCache.setNewcouponDcStatus(1);
            }
        }
        insertNewcoupon(userCache);
    }

    /**
     * @param userCache
     */
    public static void insertNewcoupon(UserCache userCache) {
        TblUserNewcoupon newcoupon = new TblUserNewcoupon();
        newcoupon.setUserId(userCache.getId());
        newcoupon.setAcStatus(userCache.getNewcouponAcStatus());
        newcoupon.setDcStatus(userCache.getNewcouponDcStatus());

        DB.userNewcouponDao.insert(newcoupon);
    }

    /**
     * 用户充电完成后  更新用户新手券状态
     *
     * @param userCache
     */
    public static void updateNewcoupon(UserCache userCache) {
        TblUserNewcoupon newcoupon = new TblUserNewcoupon();
        newcoupon.setUserId(userCache.getId());
        newcoupon.setAcStatus(userCache.getNewcouponAcStatus());
        newcoupon.setDcStatus(userCache.getNewcouponDcStatus());

        DB.userNewcouponDao.update(newcoupon);
    }

    public static int canCharge(int usrId, String usrAccount) {
        UserRealInfo realU = UserService.findUserRealInfo(usrId);
        if (null == realU) {
            return ErrorCodeConstants.INVALID_ACCOUNT;
        }
        int errorCode = realU.canCharge();
        if (errorCode > 0)
            return errorCode;

        return 0;
    }

    public static UserCache getMemOrgUser(int orgNo) {
        UserCache u = null;
        if (orgNo > 0)
            u = getUserCache(orgNo);
    	/*if(u==null)
    		u= getUserCache(usrAccount);
    	
    	if(u!=null)
    	{
    		if(u.getId()!=usrId || !u.getAccount().equals(usrAccount))
    		{
    			logger.error("getMemOrgUser usrId,usrAccount",
    					new Object[]{u.getId(),u.getAccount(),usrId,usrAccount});
    		}
    	}*/
        return u;

    }

    public static UserCache getMemUser(int usrId, String usrAccount) {
        UserCache u = null;
        if (usrId > 0)
            u = getUserCache(usrId);
        if (u == null)
            u = getUserCache(usrAccount);

        if (u != null) {
            if (u.getId() != usrId || !u.getAccount().equals(usrAccount)) {
                logger.error("getMemUser usrId,usrAccount",
                        new Object[]{u.getId(), u.getAccount(), usrId, usrAccount});
            }
        }
        return u;

    }

    public static void cleanUsrBespoke(int usrId, String usrAccount, String bespokeNo) {
        UserCache u = null;
        if (usrAccount.length() > 0) {
            u = UserService.getUserCache(usrAccount);

        }

        if (u == null && usrId > 0) {
            u = UserService.getUserCache(usrId);
        }
        if (u != null) {
            u.removeBesp(bespokeNo);
        }
    }

    public static void cleanUsrCharge(int usrId, String usrAccount, String chargeSerialNo) {
        UserCache u = null;
        if (usrAccount.length() > 0) {
            u = UserService.getUserCache(usrAccount);

        }

        if (u == null && usrId > 0) {
            u = UserService.getUserCache(usrId);
        }
        u.removeCharge(chargeSerialNo);
        UserService.putUserCache(u);
    }

    public static void insertCard(String cardInnerNo, int usrId) {

    }

    public static void cleanUsrInfo(int usrId, String usrAccount) {
        if (usrAccount.length() > 0) {
            UserCache u1 = UserService.getUserCache(usrAccount);
            if (u1 != null) {
                u1.clean();
                UserService.putUserCache(u1);
            }
        }
        if (usrId > 0) {
            UserCache u2 = UserService.getUserCache(usrId);
            if (u2 != null) {
                u2.clean();
                UserService.putUserCache(u2);
            }
        }

    }

    public static boolean checkThirdOrgNo(int orgNo)
    {
        if (orgNo == UserConstants.ORG_EC || orgNo == UserConstants.ORG_CCZC || orgNo == UserConstants.ORG_TJD
                || orgNo == UserConstants.ORG_TCEC_ECHONG || orgNo == UserConstants.ORG_EVC || orgNo == UserConstants.ORG_TCEC_SHENZHEN
                || orgNo == UserConstants.ORG_CHAT || orgNo == UserConstants.ORG_AMAP|| orgNo == UserConstants.ORG_TCEC_BEIQI || orgNo == UserConstants.ORG_TCEC_HESHUN
                || orgNo == UserConstants.ORG_TCEC_BAIDU || orgNo == UserConstants.ORG_TCEC_GUOWANG || orgNo == UserConstants.ORG_TCEC_BEIQI || orgNo == UserConstants.ORG_TCEC_NANRUI
                || orgNo == UserConstants.ORG_TCEC_NANCHONG || orgNo == UserConstants.ORG_TCEC_ALIPAY || orgNo == UserConstants.ORG_TCEC_HAINAN || orgNo == UserConstants.ORG_TCEC_XIAOJU)
            return true;
        return false;
    }
}
