package br.unicamp;
import gurobi.*;

import java.util.*;

public class Main {
    static int s = 5;
    static int c;
    static int m[][];
    static Random random = new Random(4);
    public static void main(String[] args) throws GRBException {
        GRBEnv env = new GRBEnv("mrc.log");

        instances();

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

        int l[] = {0, 4, 9, 12, 14};

        for(int i : l){
            model.addConstr(X[i], GRB.EQUAL, 1.0, "ex");
        }

        model.optimize();

        for(int i = 0; i < c; i++){
            System.out.println(X[i].get(GRB.StringAttr.VarName)
                    + " " +X[i].get(GRB.DoubleAttr.X));
        }

    }

    public static void instances(){
        ArrayList<LinkedList<Integer>> sets = new ArrayList<>();;
        int k;
        c = 0;
        for(int i = 0; i < s; i++){
            k = random.nextInt(5) + 1;
            System.out.println(k);
            LinkedList<Integer> set = new LinkedList<>();
            for(int j = 0; j < k; j++){
                set.add(c++);
            }

            sets.add(set);

            //System.out.println(set);
        }

        m = new int[c][c];

        for(int i = 0; i < s; i++){
            for(int e : sets.get(i)) {
                for (int j = i+1; j < s; j++) {
                    if(i == j) continue;
                    for(int f: sets.get(j)){
                        if(m[e][f] == 0) {
                            m[e][f] = random.nextInt(8) + 1;
                            //m[f][e] = 0;
                        }
                    }
                }
            }
        }

        for(int i = 0; i < c; i++){
            for(int j = 0; j < c; j++){
                System.out.print(m[i][j] + " ");
            }

            System.out.println();
        }
    }
}
