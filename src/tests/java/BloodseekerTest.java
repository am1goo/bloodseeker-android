import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.ITrail;
import com.am1goo.bloodseeker.trails.ClassNameTrail;
import com.am1goo.bloodseeker.trails.LibraryTrail;
import com.am1goo.bloodseeker.trails.PackageNameTrail;

class BloodseekerTest {

	@Test
	void testPackageNameTrail() {
		ITrail trail = new PackageNameTrail("com.am1goo.bloodseeker");
		boolean found = seek(trail);
		assertTrue(found);
	}
	
	@Test
	void testClassNameTrail() {				
		ITrail trail = new ClassNameTrail("com.am1goo.bloodseeker.ITrail");
		boolean found = seek(trail);
		assertTrue(found);
	}

	@Test
	void testLibraryTrail() {				
		ITrail trail = new LibraryTrail("nothingButEmpty");
		boolean found = seek(trail);
		assertFalse(found);
	}
	
	boolean seek(ITrail trail) {
		List<IResult> result = new ArrayList<IResult>();
		List<Exception> exceptions = new ArrayList<Exception>();
		trail.seek(result, exceptions);
		return result.size() > 0;
	}
}
