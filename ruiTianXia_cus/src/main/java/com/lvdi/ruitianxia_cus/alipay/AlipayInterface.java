/*** */
package com.lvdi.ruitianxia_cus.alipay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lvdi.ruitianxia_cus.activity.order.PayMethodActivity;

/**
 * @author wangcz
 * 
 */
public class AlipayInterface {
	// 商户PID
	public static final String PARTNER = "2088611941820091";
	// 商户收款账号
	public static final String SELLER = "3098814835@qq.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAMsUIFSX/ASAvdUQDjzSAOYw5spVLHZB/XERytqsR2qg+Oo9WGAdEFlDnncgxs7QRFrIixo+K4ureSF5+GLoEcUnDbaQ9Sr7hIILo7TjcMEM51FEaft55etnlGc3WxxHm9Jre3aP5XuNS49nqW/zqz200oPfNP1uIcL83JqIm+p5AgMBAAECgYEAncter7yEs08BsgtOtM2Mq1B2E+OIr77o2jG0CKPyvhSkNQZDpDMRH/sdp30NXWeQpWXMSDDkjxtG/M8URG4EV2VmOjyRUsoG2nZem3+8J2w6bhu3t43029721XqomdwdFtDgjhvyeg/zyOLzorD8nscyKGky5doOWWJQn0qwRX0CQQDptSHTBRMCNn/GG7G54pNBnbNMdy0bMeUvqX1cBxu8pe7gP1MWuTn713BKC1Dkn8i1Hzg1gXgr4Q7JdYQGesGPAkEA3nMSN4C0SGJkRpTl0R0XXae5egSdunj+7MMzkW2MOExtaPFJ51smV/+fQA+3ugNelBVePJG8zfZsws76GRp/dwJBAJl9TsOql3tik7Rmx+jPd+smSXW057juohkVdbGVMZBKYoKA1G8eI4jVS3g1ymD81/KsmFv7thMLcoy5U4Z15N0CQQDKC+6x5v7Y+DVoPnn3U9sTP/Kht2ukIZ46YpqGLuA7in+d3/tmXk4Xg6CbySrxk3nmOvpbPWwmDvt+fgx/nKfFAkEAlZ+hxNPmxrqgwdiBV3KuzoZ1X/d5W21juM4ioQiPLwWWK2yNSKLvHIiZXbQQwd84d/6nUTHAq8OcgLv2Cl5rHQ==";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;
	private PayMethodActivity mContext;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
//					Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
//					mContext.sendConfirmPayReq();
					mContext.dealAfterPaySucess();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(mContext, "支付结果确认中", Toast.LENGTH_SHORT)
								.show();

					}else if("6001".equals(resultStatus)){
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(mContext, "支付取消", Toast.LENGTH_SHORT)
								.show();
					} 
					else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT)
								.show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(mContext, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT)
						.show();
				break;
			}
			default:
				break;
			}
		};
	};

	public AlipayInterface(PayMethodActivity mContext) {
		this.mContext = mContext;
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(final String payInfo) {
	 
		 
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(mContext);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	 
	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
}
