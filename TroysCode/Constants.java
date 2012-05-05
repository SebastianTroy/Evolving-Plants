package TroysCode;

/**
 * This interface contains variables with a constant value. These constants can
 * be reached in any Class, as long as they <code> implement Constants</code>.
 * 
 * @author Sebastian Troy
 */
public interface Constants
	{
		/*
		 * This class allows the declaration of constants. I often use constants
		 * to refer to objects in an array, for example: ""arrayOfColours[0]""
		 * refers to the first member of that array, but that could be any
		 * colour. If I have the constant ""public static final byte BLUE = 0""
		 * and call ""arrayOfColours[BLUE]"" I then have an idea of what it is
		 * I'm actually accesing.
		 */
		
		public static final byte NONE = 0;
		public static final byte MAX_AGE = 1;
		public static final byte SEED_STEMS = 2;
		public static final byte LEAF_STEMS = 3;
		public static final byte CHANCE_STEMS = 4;
		public static final byte MAX_STEMS = 5;
		public static final byte STEM_ANGLE = 6;
		public static final byte SEED_ENERGY = 7;
		public static final byte LEAF_ALPHA = 8;
	}
