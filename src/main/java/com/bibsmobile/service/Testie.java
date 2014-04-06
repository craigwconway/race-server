package com.bibsmobile.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Testie {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Object[] o0 = { "a1", "a2", "a3" };
		Object[] o1 = { "b1", "b2" };
		Object[] o2 = { "c1", "c2", "c3" };
		ArrayList<Object[]> aln = new ArrayList<Object[]>();
		aln.add(o0);
		aln.add(o1);
		aln.add(o2);
		allPermutations(aln, new ArrayList<Set>(), new HashSet(), 0);
	}

	public static void allPermutations(List<Object[]> input, List<Set> output, Set work, int x){
		if(x==input.size()) return;
		for(int i=0;i<input.get(x).length;i++){
			work.add(input.get(x)[i]);
			System.out.print(input.get(x)[i]+",");
			allPermutations(input, output, work, x+1);
		}
		output.add(work);
		work.clear();
		System.out.println(";");
	}

}
