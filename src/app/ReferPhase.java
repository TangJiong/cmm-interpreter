package app;

import antlr.CmmBaseListener;
import antlr.CmmParser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

/**
 * Created by TangJiong on 2016/1/3.
 * 变量引用阶段的监听器
 */
public class ReferPhase extends CmmBaseListener {

    ParseTreeProperty<Scope> scopes;
    GlobalScope globals;
    Scope currentScope;

    public ReferPhase(GlobalScope globals, ParseTreeProperty<Scope> scopes) {
        this.globals = globals;
        this.scopes = scopes;
    }

    @Override
    public void enterProgram(CmmParser.ProgramContext ctx) {
        super.enterProgram(ctx);
        currentScope = globals;
    }

    @Override
    public void enterStmt_block(CmmParser.Stmt_blockContext ctx) {
        super.enterStmt_block(ctx);
        currentScope = scopes.get(ctx);
    }

    @Override
    public void exitStmt_block(CmmParser.Stmt_blockContext ctx) {
        super.exitStmt_block(ctx);
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void enterIf_stmt(CmmParser.If_stmtContext ctx) {
        super.enterIf_stmt(ctx);
        currentScope = scopes.get(ctx);
    }

    @Override
    public void exitIf_stmt(CmmParser.If_stmtContext ctx) {
        super.exitIf_stmt(ctx);
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void enterWhile_stmt(CmmParser.While_stmtContext ctx) {
        super.enterWhile_stmt(ctx);
        currentScope = scopes.get(ctx);
    }

    @Override
    public void exitWhile_stmt(CmmParser.While_stmtContext ctx) {
        super.exitWhile_stmt(ctx);
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void enterRead_stmt(CmmParser.Read_stmtContext ctx) {
        super.enterRead_stmt(ctx);
    }

    @Override
    public void exitRead_stmt(CmmParser.Read_stmtContext ctx) {
        super.exitRead_stmt(ctx);
        Token token = ctx.Ident().getSymbol();
        String varName = token.getText();
        Symbol var = currentScope.resolve(varName);
        if(var == null){
            System.out.println("ERROR: <"
                    + varName
                    + "> no such variable in line"
                    + token.getLine()
                    + ":" + token.getCharPositionInLine());
        }
    }

    @Override
    public void enterWrite_stmt(CmmParser.Write_stmtContext ctx) {
        super.enterWrite_stmt(ctx);
    }

    @Override
    public void exitWrite_stmt(CmmParser.Write_stmtContext ctx) {
        super.exitWrite_stmt(ctx);
        Token token = ctx.expr().value().Ident().getSymbol();
        String varName = token.getText();
        Symbol var = currentScope.resolve(varName);
        if(var == null){
            System.out.println("ERROR: <"
                    + varName
                    + "> no such variable in line"
                    + token.getLine()
                    + ":" + token.getCharPositionInLine());
        }
    }

    @Override
    public void enterAssign_stmt(CmmParser.Assign_stmtContext ctx) {
        super.enterAssign_stmt(ctx);

    }

    @Override
    public void exitAssign_stmt(CmmParser.Assign_stmtContext ctx) {
        super.exitAssign_stmt(ctx);
        Token token = ctx.value().Ident().getSymbol();
        String varName = token.getText();
        Symbol var = currentScope.resolve(varName);
        if(var == null){
            System.out.println("ERROR: <"
                    + varName
                    + "> no such variable in line"
                    + token.getLine()
                    + ":" + token.getCharPositionInLine());
        }
    }
}
