package graphic_Z;

import java.awt.event.KeyEvent;

import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.Common.SinglePoint;
//import graphic_Z.HUDs.CharDynamicHUD;
import graphic_Z.HUDs.CharLabel;
//import graphic_Z.Interfaces.ThreeDs.DrawingMethod;
//mport graphic_Z.Objects.CharMessObject;
import graphic_Z.Worlds.CharTimeSpace;
import graphic_Z.demo.CubeA;
import graphic_Z.utils.GraphicUtils;

public class RunDemo
{
	
	private static float rad(float x) {
		return x * GraphicUtils.PI / 180.0F;
	}

	public static void main(String[] args)
	{
		float fov = 1.0F;
		int scrSize = 16;
		
		CharTimeSpace testWorld = new CharTimeSpace(Short.parseShort(args[3]), Short.parseShort(args[4]), true);
		//System.out.println("testWorld created.");
		
		CubeA mainBox = new CubeA("rec3D.dat");
		//CharMessObject mainBox = testWorld.objectsManager.newMessObject(new CharMessObject("Jet.dat", 10, DrawingMethod.drawLine));
		testWorld.objectsManager.newMessObject(mainBox);
		//mainBox.specialDisplay = '';
		mainBox.setLocation(0, 0, 60);
		mainBox.visible = true;
		//System.out.println("objMan loaded.");
		CharFrapsCamera mainCamera = testWorld.visualManager.newCamera(fov);
		mainCamera.setVisibility(6400);
		//testWorld.visualManager.newHUD(args[1], 0);
        //testWorld.eventManager.switchFont(0);
		//testWorld.eventManager.setScrZoom(15);
		
		CharLabel lbl1 = testWorld.visualManager.newLabel("Test Label1", (short)3, (short)45, (short)2);
		CharLabel lbl2 = testWorld.visualManager.newLabel("Test Label2", (short)3, (short)46, (short)3);
		CharLabel lbl3 = testWorld.visualManager.newLabel("Test Label3", (short)3, (short)47, (short)4);
		
		CharLabel lbl4 = testWorld.visualManager.newLabel("Test Label4", (short)3, (short)49, (short)5);
		CharLabel lbl5 = testWorld.visualManager.newLabel("Test Label5", (short)3, (short)50, (short)6);
		CharLabel lbl6 = testWorld.visualManager.newLabel("Test Label6", (short)3, (short)51, (short)7);
		
		CharLabel lbl7 = testWorld.visualManager.newLabel("Test Label7", (short)3, (short)53, (short)8);
		/*
		CharDynamicHUD crosshair = testWorld.visualManager.newDynamicHUD(args[2], (short)20, (short)51, (short)1);
		
		crosshair.location[0] = Integer.parseInt(args[3]) >> 1;
		crosshair.location[1] = Integer.parseInt(args[4]) >> 1;
		*/
		testWorld.setRefreshRate(64);
		//System.out.println("visMan loaded.");
		mainBox.velocity_roll[0] = 0;
		mainBox.velocity_roll[1] = 0;
		int key;
		
		testWorld.getIntoGameWorld();
		while(true)
		{
			testWorld.buffStatic();
			/*
			mainBox.roll_angle[0] -= 3;
			mainBox.roll_angle[1] += 1;
			*/
			
			SinglePoint xy = testWorld.eventManager.popAMouseOpreation();
			
			if(GraphicUtils.abs(mainCamera.roll_angle[1]) > 90.0) {
				mainCamera.roll_angle[0] -= GraphicUtils.sin(rad(mainCamera.roll_angle[2])) * xy.y / 64;
				mainCamera.roll_angle[0] += GraphicUtils.cos(rad(mainCamera.roll_angle[2])) * xy.x / 64;
			} else {
				mainCamera.roll_angle[0] += GraphicUtils.sin(rad(mainCamera.roll_angle[2])) * xy.y / 64;
				mainCamera.roll_angle[0] -= GraphicUtils.cos(rad(mainCamera.roll_angle[2])) * xy.x / 64;
			}
			mainCamera.roll_angle[1] += GraphicUtils.sin(rad(mainCamera.roll_angle[2])) * xy.x / 64;
			mainCamera.roll_angle[1] += GraphicUtils.cos(rad(mainCamera.roll_angle[2])) * xy.y / 64;
			
			mainCamera.roll_angle[0] %= GraphicUtils.PIMUL2;
			mainCamera.roll_angle[1] %= GraphicUtils.PIMUL2;
			mainCamera.roll_angle[2] %= GraphicUtils.PIMUL2;
			
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
					/*
					crosshair.location[0] = (short) (Short.parseShort(args[3]) / 2);
					crosshair.location[1] = (short) (Short.parseShort(args[4]) / 2);
					
					crosshair.angle = 0;
					*/
					fov = 1;
					mainCamera.setFOV(fov);
				break;
				
				case 87://W
					mainCamera.goStreet(8.0F);
				break;
				case 65://A
					mainCamera.goLeft(8.0F);
				break;
				case 68://D
					mainCamera.goRight(8.0F);
				break;
				case 83://S
					mainCamera.goBack(8.0F);
				break;
				
				case 81://Q
					mainCamera.roll_angle[2] += 0.8;
				break;
				case 69://E
					mainCamera.roll_angle[2] -= 0.8;
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
					mainBox.velocity_roll[1] -= 0.02;
				break;
				
				case 0x62://Number 2
					mainBox.velocity_roll[1] += 0.02;
				break;
				
				case 0x61://Number 1
					mainBox.velocity_roll[0] += 0.02;
				break;
				
				case 0x63://Number 3
					mainBox.velocity_roll[0] -= 0.02;
				break;
				
				case 0x64://Number 4
					mainBox.velocity_roll[2] += 0.02;
				break;
				
				case 0x66://Number 6
					mainBox.velocity_roll[2] -= 0.02;
				break;
				/*
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
				*/
				case KeyEvent.VK_ESCAPE: System.exit(0);
			}
			testWorld.printNew();
		}
	}
}