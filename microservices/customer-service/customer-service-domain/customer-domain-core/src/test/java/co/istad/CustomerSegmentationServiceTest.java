package co.istad;

import co.istad.service.CustomerSegmentationService;
import co.istad.service.CustomerSegmentationServiceImpl;
import co.istad.valueObject.CustomerSegment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerSegmentationServiceTest {

    private CustomerSegmentationService segmentationService;

    @BeforeEach
    void setUp() {
        segmentationService = new CustomerSegmentationServiceImpl(500.0, 10, 2000.0, 50);
    }

    @Test
    @DisplayName("Default segment should be SILVER")
    void shouldReturnSilverAsDefaultSegment() {
        assertThat(segmentationService.getDefaultSegment()).isEqualTo(CustomerSegment.SILVER);
    }

    @Test
    @DisplayName("Should evaluate SILVER for low spending and low orders")
    void shouldEvaluateSilverForLowActivity() {
        CustomerSegment segment = segmentationService.evaluateSegment(3, 150.0);
        assertThat(segment).isEqualTo(CustomerSegment.SILVER);
    }

    @Test
    @DisplayName("Should evaluate GOLD when orders reach threshold")
    void shouldEvaluateGoldForOrderThreshold() {
        CustomerSegment segment = segmentationService.evaluateSegment(12, 100.0);
        assertThat(segment).isEqualTo(CustomerSegment.GOLD);
    }

    @Test
    @DisplayName("Should evaluate GOLD when spending reaches threshold")
    void shouldEvaluateGoldForSpendingThreshold() {
        CustomerSegment segment = segmentationService.evaluateSegment(5, 600.0);
        assertThat(segment).isEqualTo(CustomerSegment.GOLD);
    }

    @Test
    @DisplayName("Should evaluate PREMIUM when orders reach premium threshold")
    void shouldEvaluatePremiumForOrderThreshold() {
        CustomerSegment segment = segmentationService.evaluateSegment(55, 300.0);
        assertThat(segment).isEqualTo(CustomerSegment.PREMIUM);
    }

    @Test
    @DisplayName("Should evaluate PREMIUM when spending reaches premium threshold")
    void shouldEvaluatePremiumForSpendingThreshold() {
        CustomerSegment segment = segmentationService.evaluateSegment(15, 2500.0);
        assertThat(segment).isEqualTo(CustomerSegment.PREMIUM);
    }

    @Test
    @DisplayName("Should test tier upgrades correctly")
    void shouldValidateTierUpgrades() {
        assertThat(segmentationService.canUpgrade(CustomerSegment.SILVER, CustomerSegment.GOLD)).isTrue();
        assertThat(segmentationService.canUpgrade(CustomerSegment.GOLD, CustomerSegment.PREMIUM)).isTrue();
        assertThat(segmentationService.canUpgrade(CustomerSegment.SILVER, CustomerSegment.PREMIUM)).isTrue();
        assertThat(segmentationService.canUpgrade(CustomerSegment.PREMIUM, CustomerSegment.GOLD)).isFalse();
        assertThat(segmentationService.canUpgrade(CustomerSegment.GOLD, CustomerSegment.SILVER)).isFalse();
    }
}
