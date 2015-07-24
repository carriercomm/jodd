package jodd.petite;

import static org.junit.Assert.assertEquals;
import jodd.petite.config.AutomagicPetiteConfigurator;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

import org.junit.Before;
import org.junit.Test;

public class ParameterAnnotationTest {
	@PetiteBean
	private static class Bar {
		private final String value;

		private Bar(@PetiteInject("barValue") final String value) {
			this.value = value;
		}

		@Override
		public boolean equals(final Object obj) {
			return ((Bar) obj).value.equals(this.value);
		}
	}

	@PetiteBean
	private static class Foo {
		//TODO (mtakaki|2015-07-24): Add support to class attribute injection using names.
		private final String bar;
		private final int par;
		private final Bar barObject;

		private Foo(@PetiteInject("bar") final String bar,
				@PetiteInject("par") final int par,
				//TODO (mtakaki|2015-07-24): If there's no value, pick up the class name
				@PetiteInject final Bar barObject) {
			this.bar = bar;
			this.par = par;
			this.barObject = barObject;
		}
	}

	private PetiteContainer petite;

	@Before
	public void setupContainer() {
		this.petite = new PetiteContainer();
		final AutomagicPetiteConfigurator petiteConfigurator = new AutomagicPetiteConfigurator();
		petiteConfigurator.configure(this.petite);

		this.petite.addBean("bar", "test");
		this.petite.addBean("par", 20);
		this.petite.addBean("barValue", "this is object");
	}

	@Test
	public void test() {
		final Foo foo = this.petite.createBean(Foo.class);
		assertEquals("Unexpected injected value into an Object attribute",
				"test", foo.bar);
		assertEquals("Unexpected injected value into primitive attribute", 20,
				foo.par);
	}
}