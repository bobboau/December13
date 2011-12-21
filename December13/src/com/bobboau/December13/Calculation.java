package com.bobboau.December13;

abstract public class Calculation {
	
	abstract public String getResult();
	
	public class Opperand
	{
		public float value;
		public String description;
	}
	
	Opperand operands[];
	
	public int opperandCount()
	{
		return operands.length;
	}
	
	public String opperandDescription( int idx )
	{
		return operands[idx].description;
	}
	
	public float setOpperandvalue( int idx, float val )
	{
		return operands[idx].value = val;
	}
	
	public void init(String[] descriptions)
	{
		operands = new Opperand[descriptions.length];
		
		for(int i = 0; i<descriptions.length; i++)
		{
			operands[i] = new Opperand();
			operands[i].description = descriptions[i];
		}
	}
	
}