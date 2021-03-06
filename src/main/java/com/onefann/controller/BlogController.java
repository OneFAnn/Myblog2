package com.onefann.controller;

import com.onefann.domain.Blog;
import com.onefann.domain.BlogType;
import com.onefann.enums.ResultEnum;
import com.onefann.exception.BlogException;
import com.onefann.service.BlogService;
import com.onefann.service.BlogTypeService;
import com.onefann.util.ResultVoUtil;
import com.onefann.vo.BlogTypeArchiveVo;
import com.onefann.vo.DateArchiveVo;
import com.onefann.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by one_fann on 2017/10/25.
 */
@Slf4j
@RequestMapping("/blog")
@RestController
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogTypeService blogTypeService;

    /**
     * @return
     */


    @GetMapping("/find")
    public ResultVo find(Long id) {
        if (id == null) {
            ResultVoUtil.success(ResultEnum.BLOG_PARAMS_ERROR.getCode(), ResultEnum.BLOG_PARAMS_ERROR.getMsg());
        }
        Blog blog = blogService.findById(id);
        return ResultVoUtil.success(blog);
    }
    @GetMapping("/list")
    public ResultVo listBlog(@RequestParam(value = "page",defaultValue = "1")Integer page,
                             @RequestParam(value = "size",defaultValue = "10")Integer size) {
        Pageable pageable = new PageRequest(page-1,size,new Sort(Sort.Direction.DESC,"createTime"));
        Page<Map<String,Object>> pageList = blogService.listBlogData(pageable);
        Map<String, Object> map = new HashMap<>();
        map.put("pageList", pageList);
        map.put("currentPage", page);
        map.put("size", size);
        return ResultVoUtil.success(map);
    }

    @GetMapping("/list_by_date")
    public ResultVo listBlogByDate(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                   @RequestParam(value = "date",defaultValue = "") String date) {
        Pageable pageable = new PageRequest(page-1,size,new Sort(Sort.Direction.DESC,"create_time"));
        Page<Map<String, Object>> blogPage = blogService.listBlogDataByDate(date, pageable);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("currentPage", page);
        resultMap.put("size", size);
        resultMap.put("pageList", blogPage);
        return ResultVoUtil.success(resultMap);
    }

    @GetMapping("/archive_type")
    public ResultVo archiveBlogType() {
        List<BlogTypeArchiveVo> voList = blogService.blogTypeArchive();
        return ResultVoUtil.success(voList);
    }

    @GetMapping("/archive_date")
    public ResultVo archiveDate() {
        List<DateArchiveVo> voList = blogService.dateArchive();
        return ResultVoUtil.success(voList);
    }
    @Secured("ROLE_ADMIN")
    @GetMapping("/delete")
    public ResultVo deleteBlog(Long id) {
        if (id == null) {
            ResultVoUtil.error(ResultEnum.BLOG_PARAMS_ERROR.getCode(), ResultEnum.BLOG_PARAMS_ERROR.getMsg());
        }
        try {
            blogService.deleteBlogById(id);
        } catch (BlogException e) {
            log.error(e.getMessage());
            return ResultVoUtil.error(ResultEnum.BLOG_DELTE_ERROR.getCode(), ResultEnum.BLOG_DELTE_ERROR.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultVoUtil.error(ResultEnum.ERROR.getCode(),ResultEnum.ERROR.getMsg());
        }
        return ResultVoUtil.success(ResultEnum.BLOG_DELETE_SUCCESS.getCode(),ResultEnum.BLOG_DELETE_SUCCESS.getMsg());
    }


    @PostMapping("/save")
    @Secured("ROLE_ADMIN")
    public ResultVo save(@Valid Blog blogForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVoUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        Blog blog = new Blog();
        try {
            if (blogForm.getId() != null) {
                blog = blogService.findById(blogForm.getId());
            }
            BeanUtils.copyProperties(blogForm, blog);
            blogService.save(blog);
        } catch (BlogException e) {
            log.error(e.getMessage());
            return ResultVoUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultVoUtil.error(e.getMessage());
        }

        return ResultVoUtil.success(ResultEnum.BLOG_SAVE_SUCCESS.getCode(),ResultEnum.BLOG_SAVE_SUCCESS.getMsg());
    }


}
