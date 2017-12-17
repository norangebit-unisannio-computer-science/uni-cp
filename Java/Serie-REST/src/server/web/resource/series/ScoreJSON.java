package server.web.resource.series;

        /*
         *  Author: Raffaele Mignone
         *  Mat: 863/747
         *  Date: 14/12/17
         */

import com.google.gson.Gson;
import commons.IllegalSerieException;
import commons.Serie;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import server.backend.wrapper.RegistryAPI;

public class ScoreJSON extends ServerResource{

    @Get
    public String getScore(){
        Gson gson = new Gson();
        RegistryAPI rg = RegistryAPI.instnce();

        try{
            Serie s = rg.get(getAttribute("serie").replaceAll("%20", " "));
            return gson.toJson(s.getScore(), Float.class);
        } catch (IllegalSerieException e) {
            Status s = new Status(404);
            setStatus(s);
            return getAttribute("serie") + " not exist";
        }
    }

    @Post
    public String postScore(String payload){
        Gson gson = new Gson();
        RegistryAPI rg = RegistryAPI.instnce();

        try{
            Serie s = rg.get(getAttribute("serie").replaceAll("%20", " "));
            s.setScore(gson.fromJson(payload, Integer.class));
            return "score chamged";
        } catch (IllegalSerieException e) {
            Status s = new Status(404);
            setStatus(s);
            return getAttribute("serie") + " not exist";
        }
    }
}