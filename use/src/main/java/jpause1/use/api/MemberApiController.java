package jpause1.use.api;

import jpause1.use.domain.Member;
import jpause1.use.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller랑 @Responsebody 어노테이션 제공
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        //엔티티로 조회하면 엔티티 내부의 정보들이 노출이 된다.
        //Member 엔티티에서 주문 정보를 @JsonIgnore로 안보이게 처리하였다.
        //하지만 API는 수도 없이 많기 때문에 이렇게 Ignore로 안보이게 처리하면 좋지 않다.
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2(){
        // 이렇게 리스트로 한번 감싸줘야 유연성이 좋아짐.
        // 이렇게 하지 않으면 배열타입으로 바로 나가서 나중에 확장 불가
        List<Member> findMembers = memberService.findMembers();
        List<MemberDTO> collect = findMembers.stream()
                .map(m -> new MemberDTO(m.getUsername()))
                .collect(Collectors.toList());
        //결론 = api를 만들 때는 엔티티를 반환하지 말고 감싸서 반환하자
        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class MemberDTO {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        //RequestBody는 Json으로 온 body를 멤버에 그대로 매핑에서 쫙 넣어준다. => Json데이터를 Member로 바꿔준다.
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        //v1이랑 다른점은 클래스를 하나 만들어서 사용하는 것이다.
        //엔티티를 직접 파라미터에 넣으면 나중에 에러 찾기가 굉장히 힘들다.
        Member member = new Member();
        member.setUsername(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        //이렇게 조회와 변경을 분리시키는 것이 유지보수에 효율적이다.
        memberService.update(id, request.getName());    // 업데이트가 끝나면
        Member findMember = memberService.findOne(id);  // 쿼리가 정상적으로 실행되는지 확인차 조회
        return new UpdateMemberResponse(findMember.getId(), findMember.getUsername());
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

}
