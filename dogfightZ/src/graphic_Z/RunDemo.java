package graphic_Z;

import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.Common.SinglePoint;
import graphic_Z.HUDs.CharDynamicHUD;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.Objects.CharMessObject;
import graphic_Z.Worlds.CharTimeSpace;
import graphic_Z.utils.GraphicUtils;

public class RunDemo
{
	
	private static double rad(double x)
	{
		return x * Math.PI / 180.0;
	}

	public static void main(String[] args)
	{
		double fov = 1.0;
		int scrSize = 16;
		
		CharTimeSpace testWorld = new CharTimeSpace(Short.parseShort(args[3]), Short.parseShort(args[4]));
		//System.out.println("testWorld created.");
		
		CharMessObject mainBox = testWorld.objectsManager.newMessObject(new CharMessObject(args[0], 10, true));
		//mainBox.specialDisplay = '';
		mainBox.setLocation(0, 0, 0);
		mainBox.visible = true;
		//System.out.println("objMan loaded.");
		CharFrapsCamera mainCamera = testWorld.visualManager.newCamera(fov);
		mainCamera.setVisibility(6400);
		testWorld.visualManager.newHUD(args[1], (short)0);
		
		testWorld.visualManager.newLabel("By Lanyanzhen", (short)89, (short)28, (short)9);
		testWorld.visualManager.newLabel("201883290546", (short)90, (short)29, (short)11);
		testWorld.visualManager.newLabel("Press ESC to exit this program.", (short)75, (short)54, (short)10);
		
		CharLabel lbl1 = testWorld.visualManager.newLabel("Test Label1", (short)3, (short)45, (short)2);
		CharLabel lbl2 = testWorld.visualManager.newLabel("Test Label2", (short)3, (short)46, (short)3);
		CharLabel lbl3 = testWorld.visualManager.newLabel("Test Label3", (short)3, (short)47, (short)4);
		
		CharLabel lbl4 = testWorld.visualManager.newLabel("Test Label4", (short)3, (short)49, (short)5);
		CharLabel lbl5 = testWorld.visualManager.newLabel("Test Label5", (short)3, (short)50, (short)6);
		CharLabel lbl6 = testWorld.visualManager.newLabel("Test Label6", (short)3, (short)51, (short)7);
		
		CharLabel lbl7 = testWorld.visualManager.newLabel("Test Label7", (short)3, (short)53, (short)8);
		
		CharDynamicHUD crosshair = testWorld.visualManager.newDynamicHUD(args[2], (short)20, (short)51, (short)1);
		
		crosshair.location[0] = (short) (Short.parseShort(args[3]) / 2);
		crosshair.location[1] = (short) (Short.parseShort(args[4]) / 2);
		
		testWorld.setRefreshRate(64);
		//System.out.println("visMan loaded.");
		mainBox.velocity_roll[0] = 0;
		mainBox.velocity_roll[1] = 0;
		int key;
		
		testWorld.getIntoTheWorld();
		while(true)
		{
			testWorld.buffStatic();
			/*
			mainBox.roll_angle[0] -= 3;
			mainBox.roll_angle[1] += 1;
			*/
			
			SinglePoint xy = testWorld.eventManager.popAMouseOpreation();
			
			if(Math.abs(mainCamera.roll_angle[1]) > 90.0)
			{
				mainCamera.roll_angle[0] -= GraphicUtils.sin(rad(mainCamera.roll_angle[2])) * xy.y / 6.4;
				mainCamera.roll_angle[0] += GraphicUtils.cos(rad(mainCamera.roll_angle[2])) * xy.x / 6.4;
			}
			else
			{
				mainCamera.roll_angle[0] += GraphicUtils.sin(rad(mainCamera.roll_angle[2])) * xy.y / 6.4;
				mainCamera.roll_angle[0] -= GraphicUtils.cos(rad(mainCamera.roll_angle[2])) * xy.x / 6.4;
			}
			mainCamera.roll_angle[1] += GraphicUtils.sin(rad(mainCamera.roll_angle[2])) * xy.x / 6.4;
			mainCamera.roll_angle[1] += GraphicUtils.cos(rad(mainCamera.roll_angle[2])) * xy.y / 6.4;
			
			
			mainCamera.roll_angle[0] %= 360;
			mainCamera.roll_angle[1] %= 360;
			mainCamera.roll_angle[2] %= 360;
			
			/*
			if(mainCamera.roll_angle[1] < -90)	
				mainCamera.roll_angle[1] = -90;
			else if(mainCamera.roll_angle[1] > 90)	
				mainCamera.roll_angle[1] = 90;
			*/
			
			lbl1.setText("Camera 0 pos X: " + mainCamera.location[0]);
			lbl2.setText("Camera 0 pos Y: " + mainCamera.location[1]);
			lbl3.setText("Camera 0 pos Z: " + mainCamera.location[2]);
			
			lbl4.setText("Camera 0 ang X: " + mainCamera.roll_angle[0]);
			lbl5.setText("Camera 0 ang Y: " + mainCamera.roll_angle[1]);
			lbl6.setText("Camera 0 ang Z: " + mainCamera.roll_angle[2]);
			lbl7.setText("Camera 0 fov: " + fov);
			
			key = testWorld.eventManager.popAKeyOpreation();
			switch(key)
			{
				case 82://R
					mainBox.velocity_roll[0] = 0;
					mainBox.velocity_roll[1] = 0;
					mainBox.velocity_roll[2] = 0;
					
					mainCamera.roll_angle[0] = 0;
					mainCamera.roll_angle[1] = 0;
					mainCamera.roll_angle[2] = 0;
					
					mainCamera.location[0] = 0;
					mainCamera.location[1] = 0;
					mainCamera.location[2] = 0;
					
					mainBox.location[0] = 0;
					mainBox.location[1] = 0;
					mainBox.location[2] = 60;
					
					mainBox.roll_angle[0] = 0;
					mainBox.roll_angle[1] = 0;
					mainBox.roll_angle[2] = 0;
					
					crosshair.location[0] = (short) (Short.parseShort(args[3]) / 2);
					crosshair.location[1] = (short) (Short.parseShort(args[4]) / 2);
					
					crosshair.angle = 0;
					
					fov = 1;
					mainCamera.setFOV(fov);
				break;
				
				case 87://W
					mainCamera.goStreet(8.0);
				break;
				case 65://A
					mainCamera.goLeft(8.0);
				break;
				case 68://D
					mainCamera.goRight(8.0);
				break;
				case 83://S
					mainCamera.goBack(8.0);
				break;
				
				case 81://Q
					mainCamera.roll_angle[2] += 8.0;
				break;
				case 69://E
					mainCamera.roll_angle[2] -= 8.0;
				break;
			
				case 0x70://,
					fov -= 0.2;
					mainCamera.setFOV(fov);
				break;
				case 0x71://.
					if(fov < 10.0)
					fov += 0.2;
					mainCamera.setFOV(fov);
				break;
				
				case 74://J
					mainBox.location[1] -= 8.0;
				break;
				
				case 75://K
					mainBox.location[2] -= 8.0;
				break;
				
				case 76://L
					mainBox.location[1] += 8.0;
				break;
				
				case 85://U
					mainBox.location[0] += 8.0;
				break;
				
				case 73://I
					mainBox.location[2] += 8.0;
				break;
				
				case 79://O
					mainBox.location[0] -= 8.0;
				break;
				
				case 93://]
					scrSize += 1;
					testWorld.eventManager.setScrZoom(scrSize);
				break;
				
				case 91://[
					if(scrSize > 1)scrSize -= 1;
					testWorld.eventManager.setScrZoom(scrSize);
				break;
				
				case 0x65://Number 5
					mainBox.velocity_roll[1] -= 0.2;
				break;
				
				case 0x62://Number 2
					mainBox.velocity_roll[1] += 0.2;
				break;
				
				case 0x61://Number 1
					mainBox.velocity_roll[0] += 0.2;
				break;
				
				case 0x63://Number 3
					mainBox.velocity_roll[0] -= 0.2;
				break;
				
				case 0x64://Number 4
					mainBox.velocity_roll[2] += 0.2;
				break;
				
				case 0x66://Number 6
					mainBox.velocity_roll[2] -= 0.2;
				break;
				
				case 0x76://F7
					crosshair.location[1] += 1;
				break;
				
				case 0x77://F8
					crosshair.location[1] -= 1;
				break;
				
				case 0x78://F9
					crosshair.location[0] += 1;
				break;
				
				case 0x79://F10
					crosshair.location[0] -= 1;
				break;
				
				case 0x7a://F11
					crosshair.angle += 9;
				break;
				
				case 0x7b://F12
					crosshair.angle -= 9;
				break;
				
			}
			testWorld.printNew();
		}
	}
}