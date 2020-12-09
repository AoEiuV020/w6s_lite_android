package sz.itguy.wxlikevideo.recorder;

import android.os.Build;

import org.bytedeco.javacpp.avcodec;

public class RecorderParameters {

	private static boolean AAC_SUPPORTED  = Build.VERSION.SDK_INT >= 10;

	public int videoCodec = avcodec.AV_CODEC_ID_MPEG4;

	public int videoFrameRate = 30;

	public int videoQuality = 12;

	public int audioCodec = AAC_SUPPORTED ? avcodec.AV_CODEC_ID_AAC : avcodec.AV_CODEC_ID_AMR_NB;

	public int audioChannel = 1;

	public int audioBitrate = 96000;//192000;//AAC_SUPPORTED ? 96000 : 12200;

	public int videoBitrate = 1000000;

	public int audioSamplingRate = AAC_SUPPORTED ? 44100 : 8000;

	public String videoOutputFormat = AAC_SUPPORTED ? "mp4"  : "3gp";

    public static RecorderParameters getRecorderParameter(int currentResolution) {
        RecorderParameters parameters = new RecorderParameters();
        if (currentResolution == Constants.RESOLUTION_HIGH_VALUE) {
            parameters.audioBitrate = 128000;
            parameters.videoQuality = 0;

        } else if (currentResolution == Constants.RESOLUTION_MEDIUM_VALUE) {
            parameters.audioBitrate = 128000;
            parameters.videoQuality = 6;

        } else if (currentResolution == Constants.RESOLUTION_LOW_VALUE) {
            parameters.audioBitrate = 96000;
            parameters.videoQuality = 20;
        }
        return parameters;
    }
	
}
