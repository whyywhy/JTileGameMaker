import java.awt.Color;

public class GradientMaker {

	private Color clStart,clEnd;
	private int iSteps;
	private int iPosition = 0;
	private boolean blReversing;
	private boolean blIsReverse=false;
	private float fRed,fGreen,fBlue,fAlpha;
	
	public GradientMaker()
	{
		clStart = Color.RED;
		clEnd = Color.YELLOW;
		iSteps = 100;
		blReversing = false;
		GetColorSteps();
	}
	
	public GradientMaker(Color Start, Color End, int Steps, boolean Reversing)
	{		
		clStart = Start;
		clEnd = End;
		iSteps = Steps;
		blReversing = Reversing;
		GetColorSteps();
	}
	public GradientMaker Copy()
	{
		return new GradientMaker(clStart,clEnd,iSteps,blReversing);
	}
	
	public Color GetNextColor()
	{
		if(!blIsReverse)
		{
			if(iPosition<iSteps)
			{
				iPosition++;
			}
			else
			{
				if(blReversing)
				{
					blIsReverse=true;
				}
			}
		}
		else
		{
			if(iPosition>0)
			{
				iPosition--;
			}
			else
			{
				blIsReverse=false;
			}
		}
		return GetColorAt(iPosition);
	}
	public int[] GetIntArray(int x1,int y1,int x2,int y2)
	{
		//use distance from source as 'Position' of gradient
		int height = (int)Math.sqrt((Math.pow(Math.abs(x1-x2), 2))+(Math.pow(Math.abs(y1-y2), 2)));
		Color clNew = GetColorAt(height);
		int[] fill =  new int[] {clNew.getRed(), clNew.getGreen(), clNew.getBlue(), clNew.getAlpha()}; 
		return fill;
	}
	public void Reset()
	{
		iPosition = 0;
		blIsReverse = false;
	}
	
	public void Reset(Color Start, Color End, int Steps, boolean Reversing)
	{
		iPosition = 0;
		blIsReverse = false;
		clStart = Start;
		clEnd = End;
		iSteps = Steps;
		blReversing = Reversing;
		GetColorSteps();
	}

	public Color GetColorAt(int Position)
	{
		if(blReversing)
		{
			int iMid = iSteps/2;
			Position = Position%iSteps;
			if(Position>iMid)
			{
				Position = (iMid - (Position-iMid))*2;
			}
			else
			{
				Position = Position * 2;
			}
		}
		else
		{
			if(Position>iSteps)
			{
				Position = iSteps;
			}
		}
		int iRed = clStart.getRed()+(int)((float)Position*fRed);
		int iGreen = clStart.getGreen()+(int)((float)Position*fGreen);		
		int iBlue = clStart.getBlue()+(int)((float)Position*fBlue);
		int iAlpha = clStart.getAlpha()+(int)((float)Position*fAlpha);
		Color clNew = null;
		try
		{
			clNew = new Color(iRed,iGreen,iBlue,iAlpha);
		}
		catch(IllegalArgumentException e)
		{
			
			System.out.println("Error making color");
		}
		
		return clNew;
	}
	private void GetColorSteps()
	{
		fRed =  ((clEnd.getRed()-clStart.getRed())*((float)1.0/(float)iSteps));
		fGreen =  ((clEnd.getGreen()-clStart.getGreen())*((float)1.0/(float)iSteps));
		fBlue =  ((clEnd.getBlue()-clStart.getBlue())*((float)1.0/(float)iSteps));
		fAlpha =  ((clEnd.getAlpha()-clStart.getAlpha())*((float)1.0/(float)iSteps));
	}
	public void SetColors(Color Start, Color End)
	{
		clStart = Start;
		clEnd = End;		
	}
	public void SetSteps(int Steps)
	{
		iSteps = Steps;
		GetColorSteps();
	}
}
