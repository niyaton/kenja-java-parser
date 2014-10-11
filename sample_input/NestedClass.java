package jp.naist.sd.kenja.factextractor.test;

public class NestedClass {
	public static class Nest1{
		public int intField;
		public boolean foobar(){
			return true;
		}

		public static class NestedNest1{
			public int intField;
			public boolean foobar(){
				return false;
			}
			public static class NestedNestedNest1{
				public int intField;
				public boolean foobar(){
					return true;
				}
			}
		}
	}

	public static class Nest2{
		public double doubleField;
		public int foobar(){
			class InnnerClassInMethod1{
				public int intField;
				public int foobar2(){
					return intField;
				}
			}

			InnnerClassInMethod1 innnerClass = new InnnerClassInMethod1();
			return innnerClass.foobar2();
		}
	}

	public class Nest3{
		public float floatField;
		public boolean foobar(){
			return floatField > 0;
		}
	}

}
