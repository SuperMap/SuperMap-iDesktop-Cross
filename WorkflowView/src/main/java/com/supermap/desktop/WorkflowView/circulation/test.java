package com.supermap.desktop.WorkflowView.circulation;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by Administrator on 2017/11/28.
 */
public class test {
	public static void main(String[] args) throws Exception {
		String str = "(5++<10)";
		String str1 = "b>11&&b-->0";
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
//		engine.put("a", 1);
//		engine.put("b", 12);
//		Object result = engine.eval(str);
		while (engine.eval(str) == true && engine.eval(str1) == true) {
//			System.out.println(engine.get("a"));
//			System.out.println(engine.get("b"));
		}
//		System.out.println("结果类型:" + result.getClass().getName() + ",计算结果:" + result);
//		System.out.println(Boolean.valueOf(1>0));
//		System.out.println(Boolean.TRUE>=0);
	}
}
