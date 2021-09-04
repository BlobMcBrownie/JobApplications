package cz.chess.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Class to run all tests using one click
 *
 * @author Vojtěch Sýkora
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
            TestPieces.class,
            TestBoard.class,
       })
public class MainTests {
}
