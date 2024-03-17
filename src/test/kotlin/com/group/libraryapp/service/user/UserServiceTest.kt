package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class UserServiceTest @Autowired constructor (
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {
    @AfterEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("유저 저장이 정상동작한다.")
    fun saveUserTest() {
        //given
        val request = UserCreateRequest("최태원", null)

        //when
        userService.saveUser(request)

        //then
        val results = userRepository.findAll()

        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("최태원")
        assertThat(results[0].age).isNull()

    }

    @Test
    @DisplayName("유저 목록 조회가 정상동작한다.")
    fun getUserTest() {
        //given
        userRepository.saveAll(
            listOf(
                User("최태원", 20),
                User("김태원", null)
            )
        )

        //when
        val results = userService.getUsers()

        //then
        assertThat(results).hasSize(2)
        assertThat(results).extracting("name").containsExactlyInAnyOrder("최태원", "김태원")
        assertThat(results).extracting("age").containsExactlyInAnyOrder(20, null)
    }

    @Test
    @DisplayName("유저 이름 변경이 정상동작한다.")
    fun updateUserNameTest() {
        //given
        val saveUser = userRepository.save(User("최태원", null))
        val request = UserUpdateRequest(saveUser.id!!, "김태원")

        //when
        userService.updateUserName(request)

        //then
        val result = userRepository.findAll()[0]

        assertThat(result.name).isEqualTo("김태원")
    }

    @Test
    @DisplayName("유저 삭제가 정상동작한다.")
    fun deleteUserTest() {
        //given
        val saveUser = userRepository.save(User("최태원", null))

        //when
        userService.deleteUser(saveUser.name)

        //then
        assertThat(userRepository.findAll()).isEmpty()
    }

    @Test
    @DisplayName("대출 이력이 없는 유저도 응답에 포함된다.")
    fun getUserLoanHistoriesTest() {
        //given
        userRepository.save(User("최태원", null))

        //when
        val results = userService.getUserLoanHistories()

        //then
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("최태원")
        assertThat(results[0].books).isEmpty()
    }

    @Test
    @DisplayName("대출 이력이 많은 유저의 응답이 정상 동작한다.")
    fun getUserLoanHistoriesTest2() {
        //given
        val savedUser = userRepository.save(User("최태원", null))
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(savedUser, "책1", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUser, "책2", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUser, "책3", UserLoanStatus.RETURNED),
            )
        )

        //when
        val results = userService.getUserLoanHistories()

        //then
        assertThat(results).hasSize(3)
        assertThat(results[0].name).isEqualTo("최태원")
        assertThat(results[0].books).hasSize(3)
        assertThat(results[0].books).extracting("name").containsExactlyInAnyOrder("책1", "책2", "책3")
        assertThat(results[0].books).extracting("isReturn").containsExactlyInAnyOrder(false, false, true)
    }
}