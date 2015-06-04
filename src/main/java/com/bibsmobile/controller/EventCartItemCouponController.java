/**
 * 
 */
package com.bibsmobile.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItemCoupon;
import com.bibsmobile.model.EventType;
import com.bibsmobile.util.PermissionsUtil;
import com.bibsmobile.util.SlackUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;

/**
 * @author galen
 *
 */
@Controller
@RequestMapping("/eventcoupons")
public class EventCartItemCouponController {
	/**
     * @api {post} /eventcoupons
     * @apiGroup eventcoupons
	 * @apiName postEventCoupon
	 * @apiParam {Object} event Event object this event type belongs to, must contain id
     * @apiParam {String} code String containing coupon code. Entered by customers on checkout.
     * @apiParam {Number} discountAbsolute Absolute amount of currency discounted. discountRelative cannot also be set.
     * @apiParam {Number} discountRelative Percentage of normal price discounted. discountAbsolute cannot also be set.
     * @apiParam {Number} available Total number of coupons of this type currently available for use (decrements as used).
     * @apiParamExample {json} Sample Post 1:
     * 		{
     * 			"event": {"id": 1},
     * 			"code": "nodejs",
     * 			"discountRelative":7.5,
     * 			"available":100
     * 		}
     * @apiParamExample {json} Sample Post 2:
     * 		{
     * 			"event": {"id": 4},
     * 			"code": "devlanguage",
     * 			"discountAbsolute": 20,
     * 			"available":5
     * 		}
     * @apiSuccess (200) {Object} eventCoupon created EventCartItemCoupon object
     */
	@RequestMapping(method = RequestMethod.POST, headers="Accept=application/json")
	public ResponseEntity<String> createFromJson(@RequestBody EventCartItemCoupon eventCoupon) {
		Event event = Event.findEvent(eventCoupon.getEvent().getId());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		// check the rights the user has for event
		if (!PermissionsUtil.isEventAdmin(UserProfileUtil.getLoggedInUserProfile(), eventCoupon.getEvent())) {
			return SpringJSONUtil.returnErrorMessage("no rights for this event", HttpStatus.UNAUTHORIZED);
		}
		
		if(eventCoupon.getDiscountAbsolute() != null && eventCoupon.getDiscountRelative() != null) {
			return SpringJSONUtil.returnErrorMessage("Invalid Coupon", HttpStatus.BAD_REQUEST);
		}
		eventCoupon.setEvent(event);
		eventCoupon.persist();
		SlackUtil.logRegAddCoupon(eventCoupon, event.getName(), UserProfileUtil.getLoggedInUserProfile().getUsername());
		return new ResponseEntity<>(eventCoupon.toJson(), headers, HttpStatus.OK);
	}

	
	/**
     * @api {put} /eventcoupons
     * @apiGroup eventcoupons
	 * @apiName putEventCoupon
	 * @apiDescription Edit or delete an event coupon
	 * Enabling coupons on your event is a way to provide an incentive for early registrants.
	 * See who would register using your coupon for better sales tracking.
	 * Keeping coupons shorter in length (12 character max) allows you to easily develop
	 * a shorthand system for your coupons.ex: fall2016snr
	 * @apiParam {Number} id URL param id of event coupon to modify or delete
	 * @apiParam {Object} event Event object this event type belongs to, must contain id
     * @apiParam {String} code String containing coupon code. Entered by customers on checkout.
     * @apiParam {Number} discountAbsolute Absolute amount of currency discounted. discountRelative cannot also be set.
     * @apiParam {Number} discountRelative Percentage of normal price discounted. discountAbsolute cannot also be set.
     * @apiParam {Number} available Total number of coupons of this type currently available for use (decrements as used).
     * @apiParamExample {json} Sample Edit
     * 		{
     * 			"event": {"id": 1},
     * 			"code": "nodejs",
     * 			"discountRelative":7.5,
     * 			"available":100
     * 		}
     * @apiParamExample {json} Sample Delete
     * 		{
     * 			"event": null,
     * 			"code": "devlanguage",
     * 			"discountAbsolute": 20,
     * 			"available":5
     * 		}
     * @apiSuccess (200) {Object} eventCoupon created EventCartItemCoupon object
     */	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers="Accept=application/json")
	public ResponseEntity<String> updateFromJson(@PathVariable("id") Long id, @RequestBody EventCartItemCoupon eventCoupon) {
		EventCartItemCoupon existing = EventCartItemCoupon.findEventCartItemCoupon(id);
		Event event = Event.findEvent(existing.getEvent().getId());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		// check the rights the user has for event
		if (!PermissionsUtil.isEventAdmin(UserProfileUtil.getLoggedInUserProfile(), eventCoupon.getEvent())) {
			return SpringJSONUtil.returnErrorMessage("no rights for this event", HttpStatus.UNAUTHORIZED);
		}
		
		if(eventCoupon.getDiscountAbsolute() != null && eventCoupon.getDiscountRelative() != null) {
			return SpringJSONUtil.returnErrorMessage("Invalid Coupon", HttpStatus.BAD_REQUEST);
		}
		
		existing.setAvailable(eventCoupon.getAvailable());
		existing.setCode(eventCoupon.getCode());
		existing.setDiscountAbsolute(eventCoupon.getDiscountAbsolute());
		existing.setDiscountRelative(eventCoupon.getDiscountRelative());
		if(eventCoupon.getEvent() == null) {
			existing.setEvent(null);
		}
		existing.merge();
		return new ResponseEntity<>(headers, HttpStatus.OK);
	}
}
