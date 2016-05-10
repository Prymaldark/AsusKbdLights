/*
	AsusKbdLights v0.0.1
	by Prymal'Dark
	A simple utility to manipulate keyboard backlight intensity.
	Usage: java -cp /loc/ati/on AsusKbdLights <up|down>
	Suggested use by linking to XF86KbdBrightness{Up,Down} keys.
	Suggested to add to sudoers NOPASSWD option due to the system files.
	Automatic initialization still in testing.
	Additional features will be made available in AsusKbdLightsExt.
*/

import java.util.*;
import java.io.*;

public class AsusKbdLights
{
	// File Locations
	public static String ctrl_param = "/sys/kernel/debug/asus-nb-wmi/ctrl_param";
	public static String dev_id = "/sys/kernel/debug/asus-nb-wmi/dev_id";
	public static String devs = "/sys/kernel/debug/asus-nb-wmi/devs";

	public static String output = "";

	public static int Max = 83;
	public static int Min = 80;

	//Input Files
	public static Scanner read_ctrl_param;
	public static Scanner read_dev_id;
	public static Scanner read_devs;
	//Output Files
	public static PrintWriter write_ctrl_param;
	public static PrintWriter write_dev_id;

	public static void main(String[] args) throws IOException
	{
		boolean op = true;
		if (args[0].equals("up")) {
			op = true;
		} else if (args[0].equals("down")) {
			op = false;
		}

		try {
			//Input Files
			read_ctrl_param = new Scanner(new FileReader(ctrl_param));
			read_dev_id = new Scanner(new FileReader(dev_id));
			//Output Files
			write_ctrl_param = new PrintWriter(ctrl_param);
		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
			System.out.println("read_ctrl_param = " + read_ctrl_param.next());
			System.out.println("read_dev_id = " + read_dev_id.next());
			System.out.println("op = " + op);
		}

		int level = 0;
		switch (read_ctrl_param.next())
		{
		case "0x0":
		case "0x00000000":
			Initialize();
		case "0x00000080":
			level = 80;
			if (op) Increase(level);
			else System.out.println("Backlight already at minimum");
			break;
		case "0x00000081":
			level = 81;
			if (op) Increase(level);
			else if (!op) Decrease(level);
			break;
		case "0x00000082":
			level = 82;
			if (op) Increase(level);
			else if (!op) Decrease(level);
			break;
		case "0x00000083":
			level = 83;
			if (!op) Decrease(level);
			else System.out.println("Backlight already at maximum");
			break;
		default:
			System.out.println("Error, read_ctrl_param switch reached default");
			write_ctrl_param.printf("0x0");
		}

		try {
			read_devs = new Scanner(new FileReader(devs));
		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
			System.out.println("read_ctrl_param = " + read_ctrl_param.next());
			System.out.println("read_dev_id = " + read_dev_id.next());
			System.out.println("op = " + op);
		}

//		System.out.println("Closing files");
		//close the input files
		read_ctrl_param.close();
		read_dev_id.close();
		read_devs.close();
		//close output file
		write_ctrl_param.close();

//		System.out.println("Reopening " + devs);
		try {	//Must reopen and read read_devs for changes to apply
			read_devs = new Scanner(new FileReader(devs));
		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
		}

		output = ("read_devs = " + read_devs.next() + " " + read_devs.next()
					+ " " + read_devs.next() + " " + read_devs.next());
//		System.out.println(output);

		//close the file at $devs
		read_devs.close();

		return;
	}
	public static void Initialize() throws IOException
	{
		try {
			PrintWriter write_dev_id = new PrintWriter(dev_id);
		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
		}
		System.out.println("Initialization Begun");
		write_dev_id.printf("0x00050021");		//Set proper value in dev_id
		write_ctrl_param.printf("0x82");		//Initialize to Medium brightness
		System.out.println("Initialization Completed");

		//close output file
		write_dev_id.close();
	}
	public static void Increase(int level)
	{
		if (level != Max)
		{
			++level;	//increment
			write_ctrl_param.printf("0x" + level);
			System.out.println("Backlight Increased to " + level);
		}
	}
	public static void Decrease(int level)
	{
		if (level != Min)
		{
			--level;	//decrement
			write_ctrl_param.printf("0x" + level);
			System.out.println("Backlight Decreased to " + level);
		}
	}
}
