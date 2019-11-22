package org.wlgzs.agro_achievement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.wlgzs.agro_achievement.entity.Achievement;
import org.wlgzs.agro_achievement.entity.AchievementType;
import org.wlgzs.agro_achievement.entity.Type;
import org.wlgzs.agro_achievement.entity.User;
import org.wlgzs.agro_achievement.mapper.AchievementMapper;
import org.wlgzs.agro_achievement.mapper.AchievementTypeMapper;
import org.wlgzs.agro_achievement.mapper.TypeMapper;
import org.wlgzs.agro_achievement.service.IAchievementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wlgzs.agro_achievement.util.IoUtil;
import org.wlgzs.agro_achievement.util.RandomNumberUtils;
import org.wlgzs.agro_achievement.util.Result;
import org.wlgzs.agro_achievement.util.ResultCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 胡亚星
 * @since 2019-01-19
 */
@Service
public class AchievementServiceImpl extends ServiceImpl<AchievementMapper, Achievement> implements IAchievementService {

    @Autowired
    private TypeMapper typeMapper;

    @Autowired
    private AchievementTypeMapper achievementTypeMapper;

    //发布成果
    @Override
    public Result addAchievement(MultipartFile[] myFileNames, HttpSession session, HttpServletRequest request, Achievement achievement, String start_time, String end_time) throws FileNotFoundException {
        User user = (User) session.getAttribute("user");
        if (achievement != null) {
            String realName = "";
            String[] str = new String[myFileNames.length];
            IoUtil ioUtil = new IoUtil();
            for (int i = 0;i < myFileNames.length;i++){
                if (!myFileNames[i].getOriginalFilename().equals("")) {
                    String fileName = myFileNames[i].getOriginalFilename();
                    String fileNameExtension = fileName.substring(fileName.indexOf("."), fileName.length());
                    // 生成实际存储的真实文件名
                    realName = UUID.randomUUID().toString() + fileNameExtension;
                    // "/upload"是你自己定义的上传目录
                    String videoUrl = "/upload/" + realName;
                    System.out.println("12312====="+videoUrl);
                    ioUtil.saveFile(myFileNames[i],videoUrl);
                    str[i] = request.getContextPath() + "/upload/" + realName;
                }else{
                    System.out.println("没有文件");
                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < str.length; i++) {
                if (!myFileNames[i].getOriginalFilename().equals("")) {
                    stringBuilder.append(str[i] + ",");
                }
            }
            String pictureAddress = new String(stringBuilder);

            String typeName = achievement.getTypeName();
            QueryWrapper<Type> queryWrapperType = new QueryWrapper();
            queryWrapperType.eq("type_name", typeName);
            Type typeOne = typeMapper.selectOne(queryWrapperType);
            if (typeOne == null) {
                System.out.println("该类型不存在！");
                return new Result(ResultCode.FAIL, "该类型不存在！");
            }
            //获取现在时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime time = LocalDateTime.now();
            String localTime = formatter.format(time);
            LocalDateTime ldt = LocalDateTime.parse(localTime, formatter);
            achievement.setStatusCode("0");//需要审核
            achievement.setReleaseTime(ldt);
            achievement.setPageView(0);
            if (!start_time.equals("") && !end_time.equals("")) {
                //存入开始结束时间
                LocalDateTime timeOne = LocalDateTime.parse(start_time + " 00:00:00", formatter);
                LocalDateTime timeTwo = LocalDateTime.parse(end_time + " 00:00:00", formatter);
                achievement.setStartTime(timeOne);
                achievement.setEndTime(timeTwo);
                achievement.setPictureAddress(pictureAddress);
            }
            achievement.setUserId(user.getUserId());
            baseMapper.insert(achievement);

            QueryWrapper<Achievement> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("type_name", achievement.getTypeName()).eq("achievement_introduce", achievement.getAchievementIntroduce());
            Achievement achievement1 = baseMapper.selectOne(queryWrapper);
            AchievementType achievementType = new AchievementType();
            achievementType.setAchievementId(achievement.getAchievementId());
            achievementType.setTypeId(typeOne.getTypeId());
            achievementType.setAchievementId(achievement1.getAchievementId());
            achievementTypeMapper.insert(achievementType);

            return new Result(ResultCode.SUCCESS, "请耐心等待审核！");
        }
        System.out.println("输入正确的信息！");
        return new Result(ResultCode.FAIL, "输入正确的信息！");
    }

    //删除成果
    @Override
    public Result deleteAchievement(Integer achievementId) {
        Achievement achievement = baseMapper.selectById(achievementId);
        if (achievement != null) {
            //删除文件
            String Img = achievement.getPictureAddress();
            if (Img != null) {
                String[] arr = Img.split(",");
                for (int i = 0; i < arr.length; i++) {
                    File file = new File(System.getProperty("user.dir") + arr[i]);
                    System.out.println(file.delete());
                }
            }
            baseMapper.deleteById(achievement);
            return new Result(ResultCode.SUCCESS, "删除成功！");
        }
        return new Result(ResultCode.FAIL, "不存在！");
    }

    //修改成果
    @Override
    public Result modifyAchievement(Achievement achievement, String start_time, String end_time) {
        if (achievement != null) {
            Achievement achievement1 = baseMapper.selectById(achievement.getAchievementId());
            if (achievement1 != null) {
                achievement.setReleaseTime(achievement1.getReleaseTime());
                achievement.setStatusCode(achievement1.getStatusCode());
                achievement.setPageView(achievement1.getPageView());
                if (!"".equals(start_time) && !"".equals(end_time)) {
                    //存入开始结束时间
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime timeOne = LocalDateTime.parse(start_time + " 00:00:00", formatter);
                    LocalDateTime timeTwo = LocalDateTime.parse(end_time + " 00:00:00", formatter);
                    achievement.setStartTime(timeOne);
                    achievement.setEndTime(timeTwo);
                }
                baseMapper.updateById(achievement);
                return new Result(ResultCode.SUCCESS, "修改成功！", 1, achievement);
            }
            return new Result(ResultCode.FAIL, "该条记录不存在！");
        }
        return new Result(ResultCode.FAIL, "操作失败！");
    }

    @Override
    public Result modifyAdminAchievement(Achievement achievement, String start_time, String end_time) {
        if (achievement != null) {
            Achievement achievement1 = baseMapper.selectById(achievement.getAchievementId());
            if (achievement1 != null) {
                achievement.setReleaseTime(achievement1.getReleaseTime());
                String str = achievement1.getPictureAddress();
                if (str != null && str.contains(",")) {
                    str = str.substring(0, str.indexOf(","));
                }
                achievement.setPictureAddress(str);
                if (!"".equals(start_time) && !"".equals(end_time)) {
                    //存入开始结束时间
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime timeOne = LocalDateTime.parse(start_time + " 00:00:00", formatter);
                    LocalDateTime timeTwo = LocalDateTime.parse(end_time + " 00:00:00", formatter);
                    achievement.setStartTime(timeOne);
                    achievement.setEndTime(timeTwo);
                }
                baseMapper.updateById(achievement);
                return new Result(ResultCode.SUCCESS, "修改成功！");
            }
            return new Result(ResultCode.FAIL, "该条记录不存在！", 1, achievement);
        }
        return new Result(ResultCode.FAIL, "操作失败！", 1, null);
    }

    //按照用户查询所有成果（状态码）
    @Override
    public Result selectAchievement(Integer userId, String statusCode, int current, int limit) {
        List<Achievement> achievementList = null;
        QueryWrapper<Achievement> queryWrapper = new QueryWrapper();
        IPage<Achievement> iPage = null;
        if (statusCode == null || statusCode.equals("")) {  //查询所有
            queryWrapper.and(i -> i.eq("user_id", userId).eq("status_code", "1")).orderByDesc("achievement_id");
            Page page = new Page(current, limit);
            iPage = baseMapper.selectPage(page, queryWrapper);
            achievementList = iPage.getRecords();
            return new Result(ResultCode.SUCCESS, "", achievementList, iPage.getPages(), iPage.getCurrent());
        } else {
            queryWrapper.and(i -> i.eq("user_id", userId).eq("status_code", statusCode)).orderByDesc("achievement_id");
            Page page = new Page(current, limit);
            iPage = baseMapper.selectPage(page, queryWrapper);
            achievementList = iPage.getRecords();
            return new Result(ResultCode.SUCCESS, "", achievementList, iPage.getPages(), iPage.getCurrent());
        }
    }

    //查看成果详情
    @Override
    public Result achievementDetails(Integer achievementId) {
        Achievement achievement = baseMapper.selectById(achievementId);
        if (achievement != null) {
            return new Result(ResultCode.SUCCESS, achievement);
        }
        return new Result(ResultCode.FAIL, "不存在！");
    }

    //按照点击量排序成果
    @Override
    public Result rankingAchievement(int current, int limit) {
        List<Achievement> achievementList = null;
        QueryWrapper<Achievement> queryWrapper = new QueryWrapper();
        IPage<Achievement> iPage = null;
//        queryWrapper.and(i -> i.eq("status_code", "1").orderBy(true,false,"page_view"));
        queryWrapper.orderBy(true, false, "page_view").eq("status_code", "1");
        Page page = new Page(current, limit);
        iPage = baseMapper.selectPage(page, queryWrapper);
        achievementList = iPage.getRecords();
        if (achievementList != null) {
            return new Result(ResultCode.SUCCESS, "", achievementList, iPage.getPages(), iPage.getCurrent());
        }
        return new Result(ResultCode.FAIL, "暂无数据！");
    }

    //按分类查询成果
    @Override
    public Result selectAchieveByType(String type, int current, int limit) {
        List<Achievement> achievementList = null;//最后结果
        QueryWrapper<Achievement> queryWrapper = new QueryWrapper();
        IPage<Achievement> iPage = null;
        Page page = new Page(current, limit);

        //查询类型id
        QueryWrapper<Type> queryWrapperType = new QueryWrapper();
        queryWrapperType.eq("type_name", type);
        Type typeOne = typeMapper.selectOne(queryWrapperType);
        if (typeOne != null) {
            //查询类型id对应的记录
            QueryWrapper<AchievementType> queryWrapperAchievement = new QueryWrapper();
            queryWrapperAchievement.eq("type_id", typeOne.getTypeId());
            List<AchievementType> achievementType = achievementTypeMapper.selectList(queryWrapperAchievement);
            if (achievementType.size() <= 0) {
                return new Result(ResultCode.FAIL, "不存在！");
            }
            //将需求id存入集合
            List<Integer> achievementId = new ArrayList<>();
            for (AchievementType achievementTypeOne : achievementType) {
                achievementId.add(achievementTypeOne.getAchievementId());
            }
            queryWrapper.in("achievement_id", achievementId);
            iPage = baseMapper.selectPage(page, queryWrapper);
            achievementList = iPage.getRecords();
            return new Result(ResultCode.SUCCESS, "", achievementList, iPage.getPages(), iPage.getCurrent());
        }
        return new Result(ResultCode.FAIL, "不存在！");
    }

    @Override
    public List<Achievement> selectAchieveByTime(String home) {
        QueryWrapper<Achievement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status_code", "1").orderByDesc(true, "release_time");
        Page page;
        if ("home".equals(home)) {
            page = new Page(1, 10);
        } else {
            page = new Page(1, 3);
        }

        IPage<Achievement> iPage = baseMapper.selectPage(page, queryWrapper);
        List<Achievement> achievementList = iPage.getRecords();
        String img;
        for (int i = 0; i < achievementList.size(); i++) {
            if (achievementList.get(i).getPictureAddress() != null && achievementList.get(i).getPictureAddress().contains(",")) {
                img = achievementList.get(i).getPictureAddress();
                img = img.substring(0, img.indexOf(","));
                achievementList.get(i).setPictureAddress(img);
            }
        }
        return achievementList;
    }

    @Override
    public List<Achievement> hotAchievement() {
        List<Achievement> achievementList = baseMapper.hotAchievement();
        return achievementList;
    }

    @Override
    public IPage<Achievement> findName(String findName, int current, int limit) {
        QueryWrapper<Achievement> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("achievement_key", findName);
        Page page = new Page(current, limit);
        IPage<Achievement> iPage = baseMapper.selectPage(page, queryWrapper);
        return iPage;
    }

    @Override
    public Result saveAchievement(HttpSession session, MultipartFile[] myFileNames, HttpServletRequest request, Achievement achievement, String start_time, String end_time) throws FileNotFoundException {
        if (achievement != null) {
            String realName = "";
            String[] str = new String[myFileNames.length];
            IoUtil ioUtil = new IoUtil();
            for (int i = 0;i < myFileNames.length;i++){
                if (!myFileNames[i].getOriginalFilename().equals("")) {
                    String fileName = myFileNames[i].getOriginalFilename();
                    String fileNameExtension = fileName.substring(fileName.indexOf("."), fileName.length());
                    // 生成实际存储的真实文件名
                    realName = UUID.randomUUID().toString() + fileNameExtension;
                    // "/upload"是你自己定义的上传目录
                    String videoUrl = "/upload/" + realName;
                    System.out.println("12312====="+videoUrl);
                    ioUtil.saveFile(myFileNames[i],videoUrl);
                    str[i] = request.getContextPath() + "/upload/" + realName;
                }else{
                    System.out.println("没有文件");
                }
            }

            //文件处理（真实存储名）
//            String realName = "";
//            //存放文件储存路径
//            String[] str = new String[myFileNames.length];
//            for (int i = 0; i < myFileNames.length; i++) {
//                if (!myFileNames[i].getOriginalFilename().equals("")) {
//                    String fileName = myFileNames[i].getOriginalFilename();
//                    //截取后缀名
//                    String suffixName = fileName.substring(fileName.indexOf("."), fileName.length());
//
//                    //生成实际储存的文件名（不能重复）
//                    realName = RandomNumberUtils.getRandomFileName() + suffixName;
//                    System.out.println("realName=" + realName);
//                    System.out.println("suffixName=" + suffixName);
//                    File path = new File(ResourceUtils.getURL("classpath:").getPath());
//                    if(!path.exists()) {
//                        System.out.println("不存在！");
//                        path = new File("");
//                    }
//                    File upload = new File(path.getAbsolutePath(),"static/upload/");
//                    if(!upload.exists()) {
//                        upload.mkdirs();
//                    }
//
//
//                    // "/upload"是你自己定义的上传目录
////                    String realPath = session.getServletContext().getRealPath("/upload");
//                    File uploadFile = new File(upload.getPath(), realName);
//
//                    //上传文件
//                    try {
//                        myFileNames[i].transferTo(uploadFile);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    str[i] = request.getContextPath() + "/upload/" + realName;
//                }
//            }

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < str.length; i++) {
                if (!myFileNames[i].getOriginalFilename().equals("")) {
                    stringBuilder.append(str[i] + ",");
                }
            }
            String pictureAddress = new String(stringBuilder);

            String typeName = achievement.getTypeName();
            QueryWrapper<Type> queryWrapperType = new QueryWrapper();
            queryWrapperType.eq("type_name", typeName);
            Type typeOne = typeMapper.selectOne(queryWrapperType);
            if (typeOne == null) {
                System.out.println("该类型不存在！");
                return new Result(ResultCode.FAIL, "该类型不存在！");
            }
            //获取现在时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//            achievement.setStatusCode("0");//需要审核
            if (!"".equals(start_time) && !"".equals(end_time)) {
                //存入开始结束时间
                LocalDateTime timeOne = LocalDateTime.parse(start_time + " 00:00:00", formatter);
                LocalDateTime timeTwo = LocalDateTime.parse(end_time + " 00:00:00", formatter);
                achievement.setStartTime(timeOne);
                achievement.setEndTime(timeTwo);
                achievement.setPictureAddress(pictureAddress);
                LocalDateTime time = LocalDateTime.now();
                String localTime = formatter.format(time);
                LocalDateTime ldt = LocalDateTime.parse(localTime, formatter);
                achievement.setReleaseTime(ldt);
            }
            baseMapper.insert(achievement);

            QueryWrapper<Achievement> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("type_name", achievement.getTypeName()).eq("achievement_introduce", achievement.getAchievementIntroduce());
            Achievement achievement1 = baseMapper.selectOne(queryWrapper);
            AchievementType achievementType = new AchievementType();
            achievementType.setAchievementId(achievement.getAchievementId());
            achievementType.setTypeId(typeOne.getTypeId());
            achievementType.setAchievementId(achievement1.getAchievementId());
            achievementTypeMapper.insert(achievementType);

            return new Result(ResultCode.SUCCESS, "录入成功！");
        }
        System.out.println("输入正确的信息！");
        return new Result(ResultCode.FAIL, "输入正确的信息！");
    }

    //查询所有成果(展示)
    @Override
    public Result adminAchievementList(String findName, int current, int limit) {
        QueryWrapper<Achievement> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("achievement_name", findName).or().like("achievement_key", findName).orderByDesc("achievement_id");
        Page page = new Page(current, limit);
        IPage<Achievement> iPage = baseMapper.selectPage(page, queryWrapper);
        List<Achievement> achievementList = iPage.getRecords();

        return new Result(ResultCode.SUCCESS, "", achievementList, iPage.getPages(), iPage.getCurrent());
    }

    @Override
    public Result AchievementStatusCode(String statusCode, int current, int limit) {
        QueryWrapper<Achievement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status_code", statusCode);
        Page page = new Page(current, limit);
        IPage<Achievement> iPage = baseMapper.selectPage(page, queryWrapper);
        List<Achievement> achievementList = iPage.getRecords();

        return new Result(ResultCode.SUCCESS, "", achievementList, iPage.getPages(), iPage.getCurrent());
    }

}

