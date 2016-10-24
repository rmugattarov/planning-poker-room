package rmugattarov.planning_poker_room;

import spark.ModelAndView;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static spark.Spark.*;

/**
 * Created by rmugattarov on 22.10.2016.
 */
public class App {
    private static final ModelAndView introduceYourself = new ModelAndView(new HashMap<String, Object>(0), "velocity/introduce-yourself.vm");
    private static final Map<String, Object> model = new HashMap<>();
    private static final Map<String, String> votes = new TreeMap<>();
    private static boolean revealVotes = false;

    public static void main(String[] args) {
        Spark.staticFileLocation("/public");
        port(getHerokuAssignedPort());

        get("/", (request, response) -> {
            String userName = request.cookie("userName");
            if (userName != null && !userName.isEmpty()) {
                return new ModelAndView(model, "velocity/planning-poker-room.vm");
            } else {
                return introduceYourself;
            }
        }, new VelocityTemplateEngine());

        post("/introduce", (request, response) -> {
            String userName = request.queryParams("userName");
            response.cookie("userName", userName);
            addUser(userName);
            response.redirect("/");
            return response;
        });

        post("/vote", (request, response) -> {
            String userName = request.cookie("userName");
            String vote = request.queryParams("vote");
            vote(userName, vote);
            response.redirect("/");
            return response;
        });

        post("/revealVotes", (request, response) -> {
            revealVotes = true;
            response.redirect("/");
            return response;
        });

        post("/newVote", (request, response) -> {
            revealVotes = false;
            clearVotes();
            response.redirect("/");
            return response;
        });

        post("/clearList", (request, response) -> {
            clearList();
            response.redirect("/");
            return response;
        });

        get("/update", (request, response) -> {
            model.put("votes", votes);
            model.put("revealVotes", revealVotes);
            return new ModelAndView(model, "velocity/votes-table.vm");
        }, new VelocityTemplateEngine());
    }

    synchronized private static void addUser(String userName) {
        votes.put(userName, null);
    }

    synchronized private static void vote(String userName, String vote) {
        votes.put(userName, vote);
    }

    synchronized private static void clearVotes() {
        for (String userName : votes.keySet()) {
            votes.put(userName, null);
        }
    }

    synchronized private static void clearList() {
        votes.clear();
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
