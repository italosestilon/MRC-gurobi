package br.unicamp;
import gurobi.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    static int s;
    static int c;
    static int m[][];
    static Random random = new Random();
    public static void main(String[] args) throws GRBException {

        if(args.length < 1) {
            System.out.println("You must provide an instance path.");
            return;
        }

        String instancePath = args[0];

        getInstance(instancePath);

        GRBEnv env = new GRBEnv("mrc.log");
        GRBModel model = new GRBModel(env);
        GRBVar X[] = new GRBVar[c];
        for(int i = 0; i < c; i++){
            X[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x_"+i);
        }

        GRBVar Y[][] = new GRBVar[c][c];
        for(int i = 0; i < c; i++){
            for(int j = i+1; j < c; j++) {
                Y[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "y_" + i + "_" + j);
            }
        }

        //goal
        GRBLinExpr expr = new GRBLinExpr();
        for(int i = 0; i < c; i++){
            for(int j = i+1; j < c; j++){
                expr.addTerm(m[i][j], Y[i][j]);
            }
        }

        model.setObjective(expr, GRB.MAXIMIZE);

        //01
        for(int i = 0; i < c; i++){
            for(int j = i+1; j < c; j++){
                if(m[i][j] == 0) continue;
                model.addConstr(Y[i][j], GRB.LESS_EQUAL, X[i], "c1-"+"i_"+i+"_"+j+"-i");
                model.addConstr(Y[i][j], GRB.LESS_EQUAL, X[j], "c1-"+"i_"+i+"_"+j+"-j");
            }
        }

        //02
        for(int i = 0; i < c; i++){
            for(int j = i+1; j < c; j++){
                if(m[i][j] == 0) continue;
                GRBLinExpr expr1 = new GRBLinExpr();
                expr1.addTerm(1.0, X[i]);
                expr1.addTerm(1.0, X[j]);

                GRBLinExpr expr2 = new GRBLinExpr();
                expr2.addTerm(1.0, Y[i][j]);
                expr2.addConstant(1);

                model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "c2-"+"i_"+i+"_"+j+"");
            }
        }

        //03
        for(int i = 0; i < c; i++){
            for(int j = i+1; j < c; j++){
                if(m[i][j] != 0) continue;
                GRBLinExpr expr1 = new GRBLinExpr();
                expr1.addTerm(1.0, X[i]);
                expr1.addTerm(1.0, X[j]);


                model.addConstr(expr1, GRB.LESS_EQUAL, 1, "c2-"+"i_"+i+"_"+j+"");
            }
        }

        model.optimize();

        /*for(int i = 0; i < c; i++){
            System.out.println(X[i].get(GRB.StringAttr.VarName)
                    + " " +X[i].get(GRB.DoubleAttr.X));
        }*/

        System.out.println("Resultado: " + model.get(GRB.DoubleAttr.ObjVal));

    }

    public static void getInstance(String instancePath) {

        try(BufferedReader br = new BufferedReader(new FileReader(instancePath))) {

            String line = br.readLine();

            s = Integer.valueOf(line);
            c = 0;
            for(int i = 0; i < s; i++){
                line = br.readLine();
                c += Integer.valueOf(line);
            }

            m = new int[c][c];

            while (line != null) {

                line = br.readLine();

                if(line == null){
                    continue;
                }

                String split[] = line.split(" ");
                Integer x = Integer.valueOf(split[0]);
                Integer y = Integer.valueOf(split[1]);
                Integer v = Integer.valueOf(split[2]);
                m[x][y] = v;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
