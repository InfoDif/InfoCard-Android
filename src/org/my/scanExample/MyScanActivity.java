package org.my.scanExample;

import java.io.ByteArrayOutputStream;

import com.infodif.infocard.CreditCardType;
import com.infodif.infocard.InfoCardActivity;
import com.infodif.infocard.InfoCardOptions;
import com.infodif.infocard.CreditCardInfo;

import org.my.scanExample.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MyScanActivity extends Activity
{
	private Button scanButton;
	private TextView resultTextView;
	private ImageView imageView;
	private CheckBox checkBox;
	private int MY_SCAN_REQUEST_CODE;
	private String resultStr;
	private Bitmap blurredCardImage;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		resultStr = new String();
		resultTextView = (TextView)findViewById(R.id.resultTextView);
		scanButton = (Button)findViewById(R.id.scanButton);
		imageView = (ImageView)findViewById(R.id.imageView1);
		checkBox = (CheckBox)findViewById(R.id.checkBox1);
		MY_SCAN_REQUEST_CODE = 54;
		blurredCardImage = null;
	}

	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		resultStr = savedInstanceState.getString("resultStr");
		resultTextView.setTag(resultStr);
		int dataLength = savedInstanceState.getInt("imageDataSize");
		if( dataLength > 0 )
		{
			byte[] byteArray = savedInstanceState.getByteArray("blurredCardImage");
			blurredCardImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
			imageView.setImageBitmap(blurredCardImage);
		}
		else
		{
			imageView.setImageResource(android.R.color.transparent);
		}		
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("resultStr", resultStr);
		if( blurredCardImage != null )
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			blurredCardImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			savedInstanceState.putInt("imageDataSize", byteArray.length);
			savedInstanceState.putByteArray("blurredCardImage", byteArray);
		}
		else
		{
			savedInstanceState.putInt("imageDataSize",0);
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		scanButton.setText("Scan a credit card with infocard");
		checkBox.setChecked(true);
		checkBox.setText("FrontSide");
		resultTextView.setText(resultStr);
		resultTextView.invalidate();
		if( blurredCardImage != null )
		{
			imageView.setImageBitmap(blurredCardImage);
			imageView.invalidate();
		}
		else
		{
			imageView.setImageResource(android.R.color.transparent);
		}
		Log.w("MyScanActivity", "called onResume");
	}

	public void onScanPress(View v) {
		// This method is set up as an onClick handler in the layout xml
		// e.g. android:onClick="onScanPress"

		Intent scanIntent = new Intent(this, InfoCardActivity.class);
		InfoCardOptions options = new InfoCardOptions();
		String sdkToken = "Please use sdk token sent to you here";

		if( checkBox.isChecked() )
		{
			options.setInfocardSDKToken(sdkToken);
			options.setScanFrontSide(true);
			options.setGuideMessage(
				"Hold your card inside the frame." );
			options.setBlurCardArea(true);
			options.setManualEntryBtnText("Manual Entry");
			options.setManualEntryEnabled(true);
			options.setReturnFullSizeCardImage(true);
			options.setShowCounter(true);
			options.setShowCurrentResult(true);
			options.setShowGuideMessageWhenFocused(false);
			options.setTimeOutInSecs(15);
			options.setDrawCardTemplate(true);
			scanIntent.putExtra(InfoCardActivity.INFOCARD_OPTIONS, options);
						
			Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo_custom);
				
			if (bm != null) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				scanIntent.putExtra(InfoCardActivity.INFOCARD_OPTION_CUSTOM_LOGO, byteArray);
			}
		}
		else
		{
			options.setInfocardSDKToken(sdkToken);
			options.setScanFrontSide(false);
			options.setGuideMessage(
					"Hold your card inside the frame" );
			options.setBlurCardArea(false);
			options.setManualEntryEnabled(false);
			options.setReturnFullSizeCardImage(false);
			options.setShowCounter(false);
			options.setShowCurrentResult(false);
			options.setShowGuideMessageWhenFocused(true);
			options.setTimeOutInSecs(10);
			options.setDrawCardTemplate(false);
			scanIntent.putExtra(InfoCardActivity.INFOCARD_OPTIONS, options);			
		}
		// MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
		startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//String resultStr;
		if (data != null && data.hasExtra(InfoCardActivity.INFOCARD_SCAN_RESULT)) {
			CreditCardInfo scanResult = data.getParcelableExtra(InfoCardActivity.INFOCARD_SCAN_RESULT);

			resultStr = "";
			if( scanResult.isManualEntrySelected() )
			{
				resultStr = "Manual Entry Selected";				
			}
			if( scanResult.getCardNumber() != null )
			{
				resultStr = "Card Number: " + scanResult.getCardNumber() + "\n";
			}

			if( scanResult.getExpiryYear() != null && scanResult.getExpiryMonth() != null )
			{
				resultStr += "Expiration Date: " + scanResult.getExpiryMonth() + "/" + scanResult.getExpiryYear() + "\n";
			}

			if (scanResult.getCvv() != null) 
			{ 
				resultStr += "CVV: " + scanResult.getCvv() + "\n";
			}
			
			if( scanResult.getCardType() != CreditCardType.CreditCardTypeUnknown )
			{
				resultStr += "Card Type: ";
				if( scanResult.getCardType() == CreditCardType.CreditCardTypeAmex )
					resultStr += "American Express";
				else if( scanResult.getCardType() == CreditCardType.CreditCardTypeVisa )
					resultStr += "Visa";
				else if( scanResult.getCardType() == CreditCardType.CreditCardTypeMastercard )
					resultStr += "Mastercard";				
				resultStr += "\n";				
			}
			
			if( data.hasExtra(InfoCardActivity.INFOCARD_BLURRED_CARD_IMAGE) )
			{
				byte[] byteArray = data.getByteArrayExtra(InfoCardActivity.INFOCARD_BLURRED_CARD_IMAGE);
				blurredCardImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
				imageView.setImageBitmap(blurredCardImage);
			}
			else
			{
				blurredCardImage = null;
			}
			//scanResult.getBlurredCardImage();
		}
		else {
			resultStr = "Scan was canceled.";
		}
		resultTextView.setText(resultStr);
	}	
}

