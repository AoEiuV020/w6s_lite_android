package com.foreveross.atwork.infrastructure.model.advertisement;

import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementOpsType;
import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementOpsTypeSerializer;
import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementType;
import com.foreveross.atwork.infrastructure.model.advertisement.adEnum.AdvertisementTypeSerializer;
import com.foreveross.atwork.infrastructure.model.employee.Position;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by reyzhang22 on 17/9/16.
 */

public class AdvertisementEvent {

    @SerializedName("advertisement_id")
    public String advertisementId;

    @SerializedName("advertisement_name")
    public String advertisementName;

    @SerializedName("org_id")
    public String orgId;

    @SerializedName("ops_type")
    public String opsType;

    @SerializedName("advertisement_type")
    public String type;

    @SerializedName("kind")
    public String kind;

    @SerializedName("positions")
    public List<Position> positions;

    @SerializedName("serial_no")
    public String serialNo;

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AdvertisementOpsType.class, new AdvertisementOpsTypeSerializer());
        gsonBuilder.registerTypeAdapter(AdvertisementType.class, new AdvertisementTypeSerializer());
        return gsonBuilder.create();
    }
}
