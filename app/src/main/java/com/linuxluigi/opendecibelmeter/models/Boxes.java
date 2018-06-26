package com.linuxluigi.opendecibelmeter.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Boxes {
    private SingleBox[] boxList;

    public Boxes(JSONArray jsonArray) throws JSONException {

        boxList = new SingleBox[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject rec = jsonArray.getJSONObject(i);

            String boxName = rec.getString("name");
            String boxId = rec.getString("_id");
            JSONArray sensors = rec.getJSONArray("sensors");
            JSONObject sensor = sensors.getJSONObject(0);
            String sensorId = sensor.getString("_id");

            this.boxList[i] = new SingleBox(boxName, boxId, sensorId);
        }
    }

    /**
     * Get every box name in an ArrayList
     *
     * @return ArrayList of each box name
     */
    public List<String> getArrayList() {

        List<String> list = new ArrayList<>();
        for (SingleBox aBoxList : this.boxList) {
            list.add(
                    aBoxList.getBoxName()
            );
        }
        return list;
    }

    /**
     * Get SingleBox Object for given name
     *
     * @param boxName
     * @return SingleBox Object
     */
    public SingleBox getSingleBoxByName(String boxName) {
        for (SingleBox aBoxList : this.boxList) {
            if (aBoxList.getBoxName().equals(boxName)) {
                return aBoxList;
            }
        }
        return null;
    }
}
